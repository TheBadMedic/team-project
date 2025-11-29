import dataaccessobjects.InDiskGradientLoader;
import entity.LayerNotFoundException;
import entity.OverlayManager;
import entity.WeatherType;
import interfaceadapter.weatherlayers.legend.LegendPresenter;
import interfaceadapter.weatherlayers.legend.LegendViewModel;
import interfaceadapter.weatherlayers.layers.WeatherLayersPresenter;
import interfaceadapter.weatherlayers.layers.WeatherLayersViewModel;
import org.junit.jupiter.api.Test;
import usecase.weatherlayers.layers.ChangeLayerInputData;
import usecase.weatherlayers.layers.ChangeLayerOutputBoundary;
import usecase.weatherlayers.layers.ChangeLayerUseCase;

import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;

class ChangeLayerTest {
    private OverlayManager om = new OverlayManager(1,1);
    private WeatherLayersViewModel vm = new WeatherLayersViewModel(0.5);
    private ChangeLayerOutputBoundary presenter = new WeatherLayersPresenter(vm);
    private LegendViewModel lVm = new LegendViewModel();
    private LegendPresenter legendPresenter = new LegendPresenter(lVm);
    private ChangeLayerUseCase useCase = new ChangeLayerUseCase(om, presenter, legendPresenter, new InDiskGradientLoader());

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

    @Test
    void changeTypeTestLegend(){
        try {
            useCase.change(new ChangeLayerInputData(WeatherType.PRESSURE));
            BufferedImage viewmodelImage = lVm.getState().getImage();
            BufferedImage correctImage = new InDiskGradientLoader().getLegend(WeatherType.PRESSURE);
            boolean pass = bufferedImagesEqual(viewmodelImage, correctImage);
            assertTrue(pass);
        } catch (LayerNotFoundException e) {
            fail();
        }
    }

    @Test
    void changeTypeMultiTestLegend(){
        try {
            useCase.change(new ChangeLayerInputData(WeatherType.PRESSURE));
            BufferedImage viewmodelImage = lVm.getState().getImage();
            BufferedImage correctImage = new InDiskGradientLoader().getLegend(WeatherType.PRESSURE);
            boolean pass = bufferedImagesEqual(viewmodelImage, correctImage);

            useCase.change(new ChangeLayerInputData(WeatherType.WIND));
            BufferedImage viewmodelImageWind = lVm.getState().getImage();
            BufferedImage correctImageWind = new InDiskGradientLoader().getLegend(WeatherType.WIND);
            boolean passSecond = bufferedImagesEqual(viewmodelImageWind, correctImageWind);
            assertTrue(pass && passSecond);
        } catch (LayerNotFoundException e) {
            fail();
        }
    }

    boolean bufferedImagesEqual(BufferedImage img1, BufferedImage img2) {
        if (img1.getWidth() == img2.getWidth() && img1.getHeight() == img2.getHeight()) {
            for (int x = 0; x < img1.getWidth(); x++) {
                for (int y = 0; y < img1.getHeight(); y++) {
                    if (img1.getRGB(x, y) != img2.getRGB(x, y))
                        return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }

}
