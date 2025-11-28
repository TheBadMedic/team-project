package interfaceadapter.mapsettings.loadmapsettings;

import entity.LayerNotFoundException;
import entity.Viewport;
import entity.WeatherType;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.OsmMercator;
import usecase.mapsettings.loadmapsettings.LoadMapSettingsOutputBoundary;
import usecase.mapsettings.loadmapsettings.LoadMapSettingsOutputData;
import usecase.weatherlayers.layers.ChangeLayerInputBoundary;
import usecase.weatherlayers.layers.ChangeLayerInputData;
import view.ChangeWeatherLayersView;

import java.awt.Point;

/**
 * Presenter for automatically loading map settings on startup.
 * Applies settings directly to the viewport and overlay manager.
 */
public final class AutoLoadMapSettingsPresenter implements LoadMapSettingsOutputBoundary {

    private final Viewport viewport;
    private final ChangeLayerInputBoundary changeLayerUseCase;
    private final JMapViewer mapViewer;
    private final ChangeWeatherLayersView weatherLayersView;
    private static final OsmMercator MERCATOR = OsmMercator.MERCATOR_256;

    /**
     * Creates a presenter that applies settings directly to the viewport and overlay manager.
     *
     * @param viewport the viewport to update
     * @param changeLayerUseCase use case for changing the weather layer
     * @param mapViewer the map viewer to update with zoom level and center
     * @param weatherLayersView the weather layers view to update the dropdown selection
     */
    public AutoLoadMapSettingsPresenter(Viewport viewport,
                                        ChangeLayerInputBoundary changeLayerUseCase,
                                        JMapViewer mapViewer,
                                        ChangeWeatherLayersView weatherLayersView) {
        this.viewport = viewport;
        this.changeLayerUseCase = changeLayerUseCase;
        this.mapViewer = mapViewer;
        this.weatherLayersView = weatherLayersView;
    }

    @Override
    public void presentLoadedSettings(LoadMapSettingsOutputData outputData) {
        double latitude = outputData.getCenterLatitude();
        double longitude = outputData.getCenterLongitude();
        int zoomLevel = outputData.getZoomLevel();
        WeatherType weatherType = outputData.getWeatherType();

        // Update viewport zoom level
        viewport.setZoomLevel(zoomLevel);

        // Convert lat/lon to pixel coordinates and update viewport center
        double pixelX = MERCATOR.lonToX(longitude, zoomLevel);
        double pixelY = MERCATOR.latToY(latitude, zoomLevel);
        viewport.setPixelCenterX((int) pixelX);
        viewport.setPixelCenterY((int) pixelY);

        if (mapViewer != null) {
            mapViewer.setZoom(zoomLevel);
            mapViewer.setCenter(new Point((int) pixelX, (int) pixelY));
        }

        if (weatherType != null) {
            try {
                changeLayerUseCase.change(new ChangeLayerInputData(weatherType));

                if (weatherLayersView != null) {
                    weatherLayersView.setSelectedWeatherType(weatherType);
                }
            } catch (LayerNotFoundException e) {
 
            }
        }

        viewport.getSupport().firePropertyChange("viewportUpdated", null, viewport);
    }

    @Override
    public void presentNoSavedSettings() {
        // Use default settings
    }

    @Override
    public void presentLoadSettingsFailure(String errorMessage) {
        // Use default settings
    }
}

