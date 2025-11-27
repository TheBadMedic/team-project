package constants;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Constants {
    public static final int DEFAULT_PROGRAM_WIDTH = 800;
    public static final int DEFAULT_PROGRAM_HEIGHT = 800;
    public static final int DEFAULT_MAP_WIDTH = 600;
    public static final int DEFAULT_MAP_HEIGHT = 600;
    public static final int API_MAX_DAY_LIMIT = 3;
    public static final int HOURS_PER_DAY = 24;
    public static final int ANIMATION_INITIAL_MS_DELAY = 300;
    public static final int TICKS_PER_SECOND = 1;
    public static final int PLAY_PAUSE_BUTTON_HEIGHT = 20;
    public static final int PLAY_PAUSE_BUTTON_WIDTH = 20;
    public static final int SLIDER_WIDTH = 400;
    public static final int SLIDER_HEIGHT = 50;
    public static final int CACHE_SIZE = 200;
    public static final java.time.Duration API_MAX_DAY_LIMIT_DURATION = java.time.Duration.ofDays(3);
    public static final int TICK_LENGTH_MS = 250;
    public static final Path BOOKMARK_DATA_PATH = Paths.get("data/bookmarks.json");
    public static final Path MAP_SETTINGS_DATA_PATH = Paths.get("data/map-settings.json");
    public static final int ZOOM_THRESHOLD = 1000;
    public static final int TILE_FETCH_WORKERS = 8;
    public static final float DEFAULT_OPACITY = (float)0.5;

    private Constants() { // intentionally empty }
}
