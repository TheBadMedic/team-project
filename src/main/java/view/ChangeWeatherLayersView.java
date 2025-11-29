package view;

import entity.LayerNotFoundException;
import entity.WeatherType;
import interfaceadapter.weatherlayers.updateoverlay.UpdateOverlayController;
import interfaceadapter.weatherlayers.layers.WeatherLayersController;
import interfaceadapter.weatherlayers.layers.WeatherLayersViewModel;
import org.jetbrains.annotations.NotNull;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.interfaces.TileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource;

import javax.swing.*;
import java.awt.*;

/**
 *   <a href="https://www.thunderforest.com/docs/apikeys/">https://www.thunderforest.com/docs/apikeys/</a>
 *   Set env variable THUNDERFOREST_KEY={YOUR KEY}
 */
public class ChangeWeatherLayersView extends JPanel{
    private final JComboBox<WeatherType> weatherDropdown;
    private final JSlider slider;

    private transient WeatherLayersController layersController;
    private transient UpdateOverlayController updateController;

    public ChangeWeatherLayersView(WeatherLayersViewModel vm, JMapViewer mapViewer){
        this.setPreferredSize(new Dimension(200,200));


        slider = new JSlider(SwingConstants.HORIZONTAL, 0, 100, 50);
        slider.setPreferredSize(new Dimension(100, 50));
        slider.addChangeListener(evt -> {
                    JSlider source = (JSlider) evt.getSource();
                    if(!source.getValueIsAdjusting()) {
                        layersController.executeChangeOpacity(source.getValue());
                        updateController.update();
                    }
                }
        );

        //drop down menu for basemap options
        if (System.getenv("THUNDERFOREST_KEY") != null) {
            JComboBox<TileSource> basemapDropdown = getTileSourceJComboBox(mapViewer);
            this.add(basemapDropdown);
        }
        else{
            JLabel warning = new JLabel("<html>Missing Thunderforest API key.<br>Basemap cannot be switched.</html>");
            warning.setSize(200,200);
            this.add(warning);
        }

        weatherDropdown = new JComboBox<>(WeatherType.values());
        weatherDropdown.setPreferredSize(new Dimension(200, 100));
        weatherDropdown.addActionListener(e -> {
            try {
                layersController.executeChangeLayer((WeatherType) weatherDropdown.getSelectedItem());
                slider.setValue((int)(vm.getCurrentOpacity()*100));
                slider.setEnabled(true);
                updateController.update();
            } catch (LayerNotFoundException ex) {
                slider.setEnabled(false);
            }
        });



        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(weatherDropdown);
        this.add(slider);
    }

    @NotNull
    private static JComboBox<TileSource> getTileSourceJComboBox(JMapViewer mapViewer) {
        JComboBox<TileSource> basemapDropdown = new JComboBox<>(new TileSource[]{
                 new OsmTileSource.Mapnik(),
                 new OsmTileSource.CycleMap(),
                 new OsmTileSource.TransportMap(),
                 new OsmTileSource.LandscapeMap(),
                 new OsmTileSource.OutdoorsMap(),
                 new OsmTileSource.MobileAtlas()
         });
        basemapDropdown.addItemListener(e -> mapViewer.setTileSource((TileSource) e.getItem()));
        return basemapDropdown;
    }

    public void addLayerController (WeatherLayersController cont){
        this.layersController = cont;
    }

    public void addUpdateController (UpdateOverlayController cont){
        this.updateController = cont;
    }

    /**
     * set initial selection on startup
     */
    public void matchWeather(WeatherType type){
        weatherDropdown.setSelectedItem(type);
    }

}
