package dataaccessobjects.tilejobs;

import dataaccessinterface.TileNotFoundException;
import entity.IncompleteTile;

import java.awt.image.BufferedImage;

public interface TileCompletedListener {

    /**
     * When a tile which has been requested is successfully retrieved, passes
     * the tile meta data in the form of an {@link IncompleteTile} and the
     * BufferedImage data to all listeners.
     *
     * @param tile  the tile metadata
     * @param tileImage the image associated with the tile
     */
    void onTileCompleted(IncompleteTile tile, BufferedImage tileImage);

    /**
     * When a tile has been requested does not exist or has been unsuccessfully
     * retrieved, return the tile that was requested and the error message
     *
     * @param tile  the tile which was requested
     * @param e     the exception tied to that tile
     */
    void onTileFailed(IncompleteTile tile, TileNotFoundException e);
}
