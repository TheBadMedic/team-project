package usecase.weatherlayers.updateoverlay;

import dataaccessinterface.ImageLoader;
import constants.Constants;
import dataaccessinterface.TileNotFoundException;
import dataaccessinterface.TileRepository;
import dataaccessobjects.SimpleImageLoader;
import dataaccessobjects.tilejobs.TileCompletedListener;
import entity.*;

import java.awt.image.BufferedImage;
import java.io.IOException;

public final class UpdateOverlayUseCase implements UpdateOverlayInputBoundary, TileCompletedListener  {
    private final OverlayManager overlayManager;
    private final TileRepository tileCache;
    private final ProgramTime time;
    private final ImageLoader imageLoader;
    private final BufferedImage loadingImage;
    private final Viewport viewport;
    private final ProgramTime programTime;
    private final UpdateOverlayOutputBoundary output;
    public UpdateOverlayUseCase(OverlayManager om, TileRepository tCache, ProgramTime time, Viewport vp,
                                UpdateOverlayOutputBoundary output){
        this.overlayManager = om;
        this.tileCache = tCache;
        this.time = time;
        this.imageLoader = new SimpleImageLoader();
        this.loadingImage = getLoadingImage();
        this.viewport = vp;
        this.output = output;
        this.programTime = time;
        tileCache.addListener(this);
    }

    private BufferedImage getLoadingImage(){
        try {
            return imageLoader.getImage("img/hourglass.png");
        } catch (IOException e) {
            return new BufferedImage(255, 255, BufferedImage.TYPE_3BYTE_BGR);
        }
    }

    public void update(){
        int zoom = this.viewport.getZoomLevel();
        if (zoom > Constants.MAX_WEATHERTILE_ZOOM + 4){
            //with current tiling implemetation zooming in too much will cause a crash due to scaling an image too much,
            //so skip drawing overlay if too zoomed in.
            this.overlayManager.clearAll();
            return;
        }
        zoom = (int)Math.max(0, Math.min(Constants.MAX_WEATHERTILE_ZOOM ,zoom / 1.5));
        BoundingBox bBox = this.viewport.calculateBBox();

        //Convert to tile coords,
        //lat lon as bounding box, convert lat lon to 0-1. //Move this to boundingbox entity?
        double bBoxLY = bBox.getTopLeft().getNormalizedLatitude();
        double bBoxRY = bBox.getBottomRight().getNormalizedLatitude();
        double bBoxLX = bBox.getTopLeft().getNormalizedLongitude();
        double bBoxRX = bBox.getBottomRight().getNormalizedLongitude();

        Vector topLeft = new Vector(bBoxLX, 1 - bBoxLY);
        Vector botRight = new Vector(bBoxRX, 1 - bBoxRY);

        //convert bounding box vecs to tile grid coords based on zoom (0-6, dimension are 2^z)
        topLeft.scale(Math.pow(2, zoom));
        botRight.scale(Math.pow(2, zoom));

        //get amount of visible tiles in both direction (vp might not be a square)
        int visibleTilesX = (int)botRight.x() - (int)topLeft.x() + 1; //(15.1 to 15.6, sill 1 tile visible)
        int visibleTilesY = (int)botRight.y() - (int)topLeft.y() + 1;
        overlayManager.clear(topLeft, botRight, zoom);
        for(int i = 0; i < visibleTilesX; i++){
            for(int j = 0; j < visibleTilesY; j++) {
                int x = (int) topLeft.x() + i;
                int y = (int) topLeft.y() + j;
                if (x >= 0 && x < Math.pow(2, zoom) && y >= 0 && y < Math.pow(2, zoom)) {
                    processTile(x, y, zoom, topLeft, botRight);
                }
            }
        }
        output.updateImage(new UpdateOverlayOutputData(overlayManager.getOverlay()));

    }

    /**
     * Based on the given tile coordinate, request the tile image from the cache,
     * and if avalible, draw it to the overlay.
     * @param x The tile's x coordinate
     * @param y The tile's y coordinate
     * @param zoom The tile's zoom level
     * @param topLeft A vector representing the topleft location of the viewport, as normallized lat lon (0-1)
     * @param botRight A vector representing the bottom right location of the viewport, as normallized lat lon (0-1)
     */
    private void processTile(int x, int y, int zoom, Vector topLeft, Vector botRight) {
        TileCoords tc = new TileCoords(x, y, zoom);
        WeatherTile tile = new WeatherTile(tc, this.time.getCurrentTime(), this.overlayManager.getSelected());
        if (tileCache.inCache(tile)){
            BufferedImage imgData;
            try{
                imgData = tileCache.getTileImageData(tile);
            }
            catch (TileNotFoundException e){
                imgData = new BufferedImage(256, 256, BufferedImage.TYPE_3BYTE_BGR);
            }
            overlayManager.drawTileToOverlay(topLeft, botRight, tile, imgData);
        }
        else {
            tileCache.requestTile(tile, topLeft, botRight, viewport.getCentre(), programTime.getCurrentTime());
        }
    }

    @Override
    public void onTileCompleted(IncompleteTile tile, BufferedImage tileImage) {
        if (viewport.getCentre().equals(tile.getViewportState()) && programTime.getCurrentTime() == tile.getTime()){
            overlayManager.drawTileToOverlay(tile.getTopLeft(), tile.getBotRight(), tile.getWeatherTile(), tileImage);
            output.updateImage(new UpdateOverlayOutputData(overlayManager.getOverlay()));
        } else{
            BufferedImage tileImg = loadingImage;
            overlayManager.drawTileToOverlay(tile.getTopLeft(), tile.getBotRight(), tile.getWeatherTile(), tileImg);
            output.updateImage(new UpdateOverlayOutputData(overlayManager.getOverlay()));
        }
    }

    @Override
    public void onTileFailed(IncompleteTile tile, TileNotFoundException e) {
        BufferedImage tileImg = loadingImage;
        overlayManager.drawTileToOverlay(tile.getTopLeft(), tile.getBotRight(), tile.getWeatherTile(), tileImg);
        output.updateImage(new UpdateOverlayOutputData(overlayManager.getOverlay()));

    }
}





