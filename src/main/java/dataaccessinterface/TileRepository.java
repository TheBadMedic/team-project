package dataaccessinterface;
import dataaccessobjects.tilejobs.TileCompletedListener;
import entity.*;

import java.awt.image.BufferedImage;
import java.time.Instant;


/** Interface which returns the image data to given tiles
 *
 */
public interface TileRepository {

    /** Return the image data associated with the given tile parameters.
     *
     * @param x the x-coordinate of the tile
     * @param y the y-coordinate of the tile
     * @param zoom the zoom-level of the tile
     * @param timestamp the time which the time corresponds to
     * @param weatherType the weather type corresponding the {@link WeatherType}
     * @return the BufferedImage of the image data of the tile
     * @throws TileNotFoundException raised if the parameters do not correspond to a valid tile
     */
    BufferedImage getTileImageData(int x, int y, int zoom, java.time.Instant timestamp, WeatherType weatherType) throws TileNotFoundException;

    /** Return the image data associated with the given tile.
     *
     * @param tile the tile which the image data should be retrieved from
     * @return the BufferedImage of the image data of the tile
     * @throws TileNotFoundException raised if the tile does not correspond to valid image data
     */
    BufferedImage getTileImageData(WeatherTile tile) throws TileNotFoundException;

    /**
     * Takes a tile parameter (w/o image data) and retrieves the data associated with
     * it and adds it to cache
     *
     * @param tile        the tile to be added to cache
     * @param currentTime
     * @throws TileNotFoundException raised if the tile does not correspond to valid image data
     */
    void requestTile(WeatherTile tile, Vector topLeft, Vector botRight, Location tileCoords, Instant currentTime);

    /**
     * Add a {@link TileCompletedListener} listener to this repository such that whenever a requested tile
     * has finished fetching, the Tile and image data is passed back to all listeners
     *
     * @param listener  the listener for which to be attached to this repositroy
     */
    void addListener(TileCompletedListener listener);

    /** Returns whether the given tile is stored in cache
     *
     * @param tile  tile to check for being in presence
     * @return  tile is stored in cache
     */
    boolean inCache(WeatherTile tile);
}
