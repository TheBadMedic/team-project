package dataaccessobjects;

import dataaccessinterface.GradientLegendLoader;
import entity.WeatherType;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.EnumMap;


public class InDiskGradientLoader implements GradientLegendLoader {
    private final EnumMap<WeatherType, BufferedImage> legends;
    public InDiskGradientLoader(){
        legends = new EnumMap<>(WeatherType.class);
        loadLegends();
    }

    @Override
    public BufferedImage getLegend(WeatherType type){
        return legends.get(type);
    }

    private void loadLegends(){
        BufferedImage blank;

        try{
            File imageFile = new File("img/legends/legendblank.png");
            blank = ImageIO.read(imageFile);
        } catch (IOException e) {
            blank = new BufferedImage(1,1,1);
        }
        for(WeatherType type: WeatherType.values()){
            try{
                BufferedImage legendImg = null;
                File imageFile = new File("img/legends/legend" + type.toString().replace(" ","").toLowerCase() + ".png");
                legendImg = ImageIO.read(imageFile);
                legends.put(type, legendImg);
            } catch (RuntimeException | IOException e) {
                legends.put(type, blank);
            }

        }
    }
}
