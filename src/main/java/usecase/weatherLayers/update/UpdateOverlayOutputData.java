package usecase.weatherLayers.update;

import java.awt.image.BufferedImage;

public class UpdateOverlayOutputData {
    private final BufferedImage image;
    public UpdateOverlayOutputData(BufferedImage img){
        this.image = img;
    }

    public BufferedImage getImage(){
        return this.image;
    }
}
