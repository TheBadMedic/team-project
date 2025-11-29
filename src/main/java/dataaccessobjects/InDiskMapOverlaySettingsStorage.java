package dataaccessobjects;

import dataaccessinterface.SavedMapOverlaySettings;
import entity.Location;
import entity.WeatherType;
import org.json.JSONObject;
import java.nio.charset.StandardCharsets;

import java.io.IOException;
import java.nio.file.*;

/**
 * Disk-backed implementation of {@link SavedMapOverlaySettings} that
 * persists the last map center and zoom level as a JSON object in a file.
 */

public final class InDiskMapOverlaySettingsStorage implements SavedMapOverlaySettings {

    private static final String KEY_CENTER_LATITUDE = "centerLatitude";
    private static final String KEY_CENTER_LONGITUDE = "centerLongitude";
    private static final String KEY_ZOOM_LEVEL = "zoomLevel";
    private static final String KEY_WEATHER_TYPE = "weatherType";

    /**
     * Path to the JSON file used for persistence.
     */
    private final Path filePath;

    /**
     * Creates a storage instance that persists map settings to the given file.
     *
     * @param filePath path to the JSON file used for persistence
     */
    public InDiskMapOverlaySettingsStorage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Returns whether there are valid saved map settings present on disk.
     *
     * <p>Valid settings are considered to exist if the JSON file can be
     * read and contains entries for {@code centerLatitude},
     * {@code centerLongitude}, and {@code zoomLevel}. If the file cannot
     * be read for any reason, this method returns {@code false}.</p>
     *
     * @return {@code true} if valid saved settings exist, {@code false} otherwise
     */
    @Override
    public synchronized boolean hasSavedSettings() {
        try {
            JSONObject obj = readSettings();
            return obj.has(KEY_CENTER_LATITUDE)
                    && obj.has(KEY_CENTER_LONGITUDE)
                    && obj.has(KEY_ZOOM_LEVEL);
            // weatherType is optional for backward compatibility
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Returns the last saved center location of the map.
     *
     * <p>This method expects valid saved settings to be present on disk.
     * Callers should check {@link #hasSavedSettings()} first
     * before invoking this method.</p>
     *
     * @return the last saved center {@link Location}
     * @throws MapOverlaySettingsPersistenceException if reading or parsing
     *         the stored settings fails for any reason
     */
    @Override
    public synchronized Location getSavedCenterLocation() {
        try {
            JSONObject obj = readSettings();
            double lat = obj.getDouble(KEY_CENTER_LATITUDE);
            double lon = obj.getDouble(KEY_CENTER_LONGITUDE);

            return new Location(lat, lon);
        } catch (IOException | RuntimeException e) {
            throw new MapOverlaySettingsPersistenceException(
                    "Failed to read map overlay settings from " + filePath, e);
        }
    }

    /**
     * Returns the last saved zoom level of the map.
     *
     * <p>This method expects valid saved settings to be present on disk.
     * Callers should check {@link #hasSavedSettings()} first
     * before invoking this method.</p>
     *
     * @return the last saved zoom level
     * @throws MapOverlaySettingsPersistenceException if reading or parsing
     *         the stored settings fails for any reason
     */
    @Override
    public synchronized int getSavedZoomLevel() {
        try {
            JSONObject obj = readSettings();
            return obj.getInt(KEY_ZOOM_LEVEL);
        } catch (IOException | RuntimeException e) {
            throw new MapOverlaySettingsPersistenceException(
                    "Failed to read map overlay settings from " + filePath, e);
        }
    }

    /**
     * Returns the last saved weather type.
     *
     * <p>This method expects valid saved settings to be present on disk.
     * Callers should check {@link #hasSavedSettings()} first
     * before invoking this method.</p>
     *
     * @return the last saved weather type, or null if not saved
     * @throws MapOverlaySettingsPersistenceException if reading or parsing
     *         the stored settings fails for any reason
     */
    @Override
    public synchronized WeatherType getSavedWeatherType() {
        try {
            JSONObject obj = readSettings();
            if (obj.has(KEY_WEATHER_TYPE)) {
                return parseWeatherType(obj.getString(KEY_WEATHER_TYPE));
            }
            return null;
        } catch (IOException | RuntimeException e) {
            throw new MapOverlaySettingsPersistenceException(
                    "Failed to read map overlay settings from " + filePath, e);
        }
    }

    /**
     * Persists the given map center location, zoom level, and weather type to disk.
     *
     * <p>The values are stored as a JSON object with keys
     * {@code centerLatitude}, {@code centerLongitude}, {@code zoomLevel}, and {@code weatherType}.
     * Save is performed via a temporary file and an atomic move to
     * reduce the chance of partial writes.</p>
     *
     * @param centerLocation the current center {@link Location} of the map
     * @param zoomLevel      the current zoom level of the map
     * @param weatherType    the currently selected weather type
     * @throws MapOverlaySettingsPersistenceException if writing to disk fails
     */
    @Override
    public synchronized void save(Location centerLocation, int zoomLevel, WeatherType weatherType) {
        JSONObject obj = new JSONObject();

        obj.put(KEY_CENTER_LATITUDE, centerLocation.getLatitude());
        obj.put(KEY_CENTER_LONGITUDE, centerLocation.getLongitude());
        obj.put(KEY_ZOOM_LEVEL, zoomLevel);
        if (weatherType != null) {
            obj.put("weatherType", weatherType);
        }

        try {
            writeSettings(obj);
        } catch (IOException e) {
            throw new MapOverlaySettingsPersistenceException(
                    "Failed to persist map overlay settings to " + filePath, e);
        }
    }

    /**
     * Reads the JSON representation of the saved map settings from disk.
     *
     * <p>If the file does not exist, is empty, or contains invalid JSON,
     * this method returns an empty {@link JSONObject} instead of throwing
     * an error.</p>
     *
     * @return a {@link JSONObject} representing the saved settings
     * @throws IOException if an I/O error occurs while reading the file
     */
    private JSONObject readSettings() throws IOException {
        if (!Files.exists(filePath)) {
            return new JSONObject();
        }

        String raw = Files.readString(filePath, StandardCharsets.UTF_8).trim();
        if (raw.isEmpty()) {
            return new JSONObject();
        }

        try {
            return new JSONObject(raw);
        } catch (RuntimeException parseError) {
            // Likely a corrupt file. Treat it as if there are no settings.
            return new JSONObject();
        }
    }

    /**
     * Writes the given JSON object representing the map settings to disk.
     *
     * <p>This method writes to a temporary file first and then atomically
     * moves the file into place, replacing any existing file. Parent
     * directories are created if necessary.</p>
     *
     * @param obj the {@link JSONObject} to write to disk
     * @throws IOException if an I/O error occurs while writing the file
     */
    private void writeSettings(JSONObject obj) throws IOException {
        byte[] bytes = obj.toString(2).getBytes(StandardCharsets.UTF_8);

        Path dir = filePath.getParent();
        if (dir != null) {
            Files.createDirectories(dir);
        }

        Path tmp;
        if (dir != null) {
            tmp = Files.createTempFile(dir, "map-settings", ".tmp");
        } else {
            // Go back to system temp directory if there is no parent.
            tmp = Files.createTempFile("map-settings", ".tmp");
        }

        Files.write(tmp, bytes, StandardOpenOption.TRUNCATE_EXISTING);

        Files.move(tmp, filePath,
                StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.ATOMIC_MOVE);
    }

    private WeatherType parseWeatherType(String weatherTypeStr) {
        try {
            return WeatherType.valueOf(weatherTypeStr);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}