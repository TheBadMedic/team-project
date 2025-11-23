package dataaccessobjects.tilejobs;

import entity.*;
import kotlin.Pair;

import java.awt.image.BufferedImage;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;

/** Stores the data necessary to fetch a tile from an API. Their lifespan
 * are tied directly to the existence of {@link TileJobSystem}
 */
public class TileJob {

    private final CompletableFuture<BufferedImage> future = new CompletableFuture<>();
    private final IncompleteTile tileData;
    private final Pair<IncompleteTile, CompletableFuture<BufferedImage>> futureData;


    public TileJob(WeatherTile tile, Vector topLeft, Vector botRight, Location viewportState, Instant time) {
        this.tileData = new IncompleteTile(topLeft, botRight, viewportState, tile, time);
        this.futureData = new Pair<>(this.tileData, this.future);
    }

    /**
     * Return the {@link WeatherTile} associated with this tile job
     * @return the WeatherTile which this job is fetchig the image data for
     */
    public WeatherTile getTile() {
        return tileData.getWeatherTile();
    }

    /**
     * Return a 2-tuple with the tile meta-data and the {@link CompletableFuture} with
     * of the BufferedImage
     *
     * @return the pair of the tile meta data in <code>component1</code> and the future in <code>component2</code>
     */
    public Pair<IncompleteTile, CompletableFuture<BufferedImage>> getFuture(){
        return futureData;
    }

}