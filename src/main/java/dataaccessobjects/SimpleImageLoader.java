package dataaccessobjects;

import dataaccessinterface.ImageLoader;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SimpleImageLoader implements ImageLoader {

    @Override
    public BufferedImage getImage(String file) throws IOException{
                File imageFile = new File(file);
                return ImageIO.read(imageFile);
        }
    }

