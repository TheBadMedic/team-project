package dataaccessinterface;
import java.awt.image.BufferedImage;
import java.io.IOException;

public interface ImageLoader {
    /**
     * Return a BufferedImage corrosponding to the requested file path. throws IOException if the requested file
     * is not found.
     * @param file the file path of the image.
     * @return A BufferedImage of the input file.
     */
    BufferedImage getImage(String file) throws IOException;
}
