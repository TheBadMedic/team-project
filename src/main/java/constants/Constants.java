package constants;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Constants {
    public static final int DEFAULT_PROGRAM_WIDTH = 800;
    public static final int DEFAULT_PROGRAM_HEIGHT = 800;
    public static final int DEFAULT_MAP_WIDTH = 600;
    public static final int DEFAULT_MAP_HEIGHT = 600;
    public static final Path BOOKMARK_DATA_PATH = Paths.get("data/bookmarks.json");
    public static final int ZOOM_THRESHOLD = 1000;
}
