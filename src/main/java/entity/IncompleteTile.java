package entity;

import java.time.Instant;

/**
 * Placeholder tile for a tile displayed on the overlay
 * which has no image data attached to yet while being fetched from the API
 */

public class IncompleteTile {
    private final Vector topLeft;
    private final Vector botRight;
    private final Location viewportState;
    private final WeatherTile tile;
    private final Instant time;

    public IncompleteTile(Vector topLeft, Vector botRight, Location viewportState, WeatherTile tile, Instant time){
        this.topLeft = topLeft;
        this.botRight = botRight;
        this.viewportState = viewportState;
        this.tile = tile;
        this.time = time;
    }

    public Vector getTopLeft() {
        return topLeft;
    }

    public Vector getBotRight() {
        return botRight;
    }

    public WeatherTile getWeatherTile() {
        return tile;
    }

    public Location getViewportState() { return viewportState; }

    public Instant getTime() { return time; }

}
