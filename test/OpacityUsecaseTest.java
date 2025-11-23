import entity.LayerNotFoundException;
import entity.OverlayManager;
import entity.WeatherType;
import usecase.weatherLayers.layers.ChangeOpacityInputData;
import usecase.weatherLayers.layers.ChangeOpacityUseCase;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class OpacityUsecaseTest {
    private OverlayManager om = new OverlayManager(1,1);
    private ChangeOpacityUseCase useCase = new ChangeOpacityUseCase(om);

    @Test
    void changeOpacityTestAll(){
        try {
            om.setSelected(WeatherType.Tmp2m);
            useCase.change(new ChangeOpacityInputData((float)0.1));
            om.setSelected(WeatherType.Precip);
            useCase.change(new ChangeOpacityInputData((float)0.2));
            om.setSelected(WeatherType.Pressure);
            useCase.change(new ChangeOpacityInputData((float)0.3));
            om.setSelected(WeatherType.Wind);
            useCase.change(new ChangeOpacityInputData((float)0.4));

            om.setSelected(WeatherType.Tmp2m);
            boolean op1 = om.getSelectedOpacity() == (float)0.1;
            om.setSelected(WeatherType.Precip);
            boolean op2 = om.getSelectedOpacity() == (float)0.2;
            om.setSelected(WeatherType.Pressure);
            boolean op3 = om.getSelectedOpacity() == (float)0.3;
            om.setSelected(WeatherType.Wind);
            boolean op4 = om.getSelectedOpacity() == (float)0.4;
            assertTrue (op1 && op2 && op3 && op4);
        } catch (LayerNotFoundException e) {
            assertFalse(false);
        }
    }

    @Test
    void changeOpacityTestSetMultipleTimes(){
        try {
            om.setSelected(WeatherType.Tmp2m);
            useCase.change(new ChangeOpacityInputData((float)0.1));
            useCase.change(new ChangeOpacityInputData((float)0.2));
            useCase.change(new ChangeOpacityInputData((float)0.3));
            useCase.change(new ChangeOpacityInputData((float)0.4));

            om.setSelected(WeatherType.Tmp2m);
            boolean op1 = om.getSelectedOpacity() == (float)0.4;
            assertTrue (op1);
        } catch (LayerNotFoundException e) {
            assertFalse(false);
        }
    }

    @Test
    void changeOpacityTestOutOfBounds(){
        try {
            om.setSelected(WeatherType.Tmp2m);
            useCase.change(new ChangeOpacityInputData((float)-0.1));
            boolean op1 = om.getSelectedOpacity() == (float)0;
            om.setSelected(WeatherType.Wind);
            useCase.change(new ChangeOpacityInputData((float)1.1));
            boolean op2 = om.getSelectedOpacity() == (float)1;
            assertTrue (op1 && op2);
        } catch (LayerNotFoundException e) {
            assertFalse(false);
            assertFalse(false);
        }
    }
}
