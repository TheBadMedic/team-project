import entity.LayerNotFoundException;
import entity.OverlayManager;
import entity.WeatherType;
import interfaceadapter.weatherLayers.LegendPresenter;
import interfaceadapter.weatherLayers.LegendViewModel;
import interfaceadapter.weatherLayers.WeatherLayersPresenter;
import interfaceadapter.weatherLayers.WeatherLayersViewModel;
import org.junit.jupiter.api.Test;
import usecase.weatherLayers.layers.ChangeLayerInputData;
import usecase.weatherLayers.layers.ChangeLayerOutputBoundary;
import usecase.weatherLayers.layers.ChangeLayerUseCase;

import static org.junit.jupiter.api.Assertions.*;

public class ChangeLayerTest {
    private OverlayManager om = new OverlayManager(1,1);
    private WeatherLayersViewModel vm = new WeatherLayersViewModel(0.5);
    private ChangeLayerOutputBoundary presenter = new WeatherLayersPresenter(vm);
    private LegendViewModel lVm = new LegendViewModel();
    private LegendPresenter legendPresenter = new LegendPresenter(lVm);
    private ChangeLayerUseCase useCase = new ChangeLayerUseCase(om, presenter, legendPresenter);

    @Test
    void invalidTypeThrows(){
        try {
            useCase.change(new ChangeLayerInputData(WeatherType.INVALID));
        } catch (LayerNotFoundException e) {
            assertTrue(true);
        }
    }
    @Test
    void changeType(){
        try {
            useCase.change(new ChangeLayerInputData(WeatherType.PRESSURE));
            assertSame(WeatherType.PRESSURE, om.getSelected());
        } catch (LayerNotFoundException e) {
            fail();
        }
    }
    @Test
    void changeTypeMulti(){
        try {
            useCase.change(new ChangeLayerInputData(WeatherType.PRESSURE));
            useCase.change(new ChangeLayerInputData(WeatherType.WIND));
            useCase.change(new ChangeLayerInputData(WeatherType.TMP2M));
            useCase.change(new ChangeLayerInputData(WeatherType.WIND));
            assertSame(WeatherType.WIND, om.getSelected());
        } catch (LayerNotFoundException e) {
            fail();
        }
    }

}
