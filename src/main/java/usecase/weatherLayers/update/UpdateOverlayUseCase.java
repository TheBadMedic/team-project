package usecase.weatherLayers.update;

import dataaccessinterface.TileNotFoundException;
import dataaccessinterface.TileRepository;
import dataaccessobjects.tilejobs.TileCompletedListener;
import entity.*;

import java.awt.image.BufferedImage;

public final class UpdateOverlayUseCase implements UpdateOverlayInputBoundary, TileCompletedListener  {
    private final OverlayManager overlayManager;
    private final TileRepository tileCache;
    private final ProgramTime time;
    private final Viewport viewport;
    private final ProgramTime programTime;
    private final UpdateOverlayOutputBoundary output;

    public UpdateOverlayUseCase(OverlayManager om, TileRepository tCache, ProgramTime time, Viewport vp,
                                UpdateOverlayOutputBoundary output){
        this.overlayManager = om;
        this.tileCache = tCache;
        this.time = time;
        this.viewport = vp;
        this.output = output;
        this.programTime = time;
        tileCache.addListener(this);
    }

    public void update(){
        int zoom = this.viewport.getZoomLevel();
        if (zoom > 10){
            //with current tiling implemetation zooming in too much will cause a crash due to scaling an image too much,
            //so skip drawing overlay if too zoomed in.
            this.overlayManager.clearAll();
            return;
        }
        zoom = (int)Math.max(0, Math.min(6,zoom / 1.5));
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
        int visibleTilesX = (int)botRight.x - (int)topLeft.x + 1; //(15.1 to 15.6, sill 1 tile visible)
        int visibleTilesY = (int)botRight.y - (int)topLeft.y + 1;
        overlayManager.clear(topLeft, botRight, zoom);
        for(int i = 0; i < visibleTilesX; i++){
            for(int j = 0; j < visibleTilesY; j++) {
                int x = (int) topLeft.x + i;
                int y = (int) topLeft.y + j;
                if (x >= 0 && x < Math.pow(2, zoom) && y >= 0 && y < Math.pow(2, zoom)) {
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
            }
        }
        output.updateImage(new UpdateOverlayOutputData(overlayManager.getOverlay()));

    }

    @Override
    public void onTileCompleted(IncompleteTile tile, BufferedImage tileImage) {
        if (viewport.getCentre().equals(tile.getViewportState()) && programTime.getCurrentTime() == tile.getTime()){
            overlayManager.drawTileToOverlay(tile.getTopLeft(), tile.getBotRight(), tile.getWeatherTile(), tileImage);
            output.updateImage(new UpdateOverlayOutputData(overlayManager.getOverlay()));
        } else{
            BufferedImage tileImg = new BufferedImage(256, 256, BufferedImage.TYPE_3BYTE_BGR);
            overlayManager.drawTileToOverlay(tile.getTopLeft(), tile.getBotRight(), tile.getWeatherTile(), tileImg);
            output.updateImage(new UpdateOverlayOutputData(overlayManager.getOverlay()));
        }
    }

    @Override
    public void onTileFailed(IncompleteTile tile, TileNotFoundException e) {
        BufferedImage tileImg = new BufferedImage(256, 256, BufferedImage.TYPE_3BYTE_BGR);
        overlayManager.drawTileToOverlay(tile.getTopLeft(), tile.getBotRight(), tile.getWeatherTile(), tileImg);
        output.updateImage(new UpdateOverlayOutputData(overlayManager.getOverlay()));

    }
}





