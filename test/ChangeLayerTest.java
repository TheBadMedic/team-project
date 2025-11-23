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
import usecase.weatherLayers.layers.ChangeOpacityUseCase;

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
            useCase.change(new ChangeLayerInputData(WeatherType.Invalid));
        } catch (LayerNotFoundException e) {
            assertTrue(true);
        }
    }
    @Test
    void changeType(){
        try {
            useCase.change(new ChangeLayerInputData(WeatherType.Pressure));
            assertSame(WeatherType.Pressure, om.getSelected());
        } catch (LayerNotFoundException e) {
            fail();
        }
    }
    @Test
    void changeTypeMulti(){
        try {
            useCase.change(new ChangeLayerInputData(WeatherType.Pressure));
            useCase.change(new ChangeLayerInputData(WeatherType.Wind));
            useCase.change(new ChangeLayerInputData(WeatherType.Tmp2m));
            useCase.change(new ChangeLayerInputData(WeatherType.Wind));
            assertSame(WeatherType.Wind, om.getSelected());
        } catch (LayerNotFoundException e) {
            fail();
        }
    }

}
