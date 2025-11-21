package constants;

import java.awt.*;
import java.io.File;
import java.io.IOException;
// TODO: this mgiht not be clean?
public class DefaultThemes {

    public static final Color BG_COLOUR =Color.BLUE;

    public static final Font BOLD_BODY_FONT;
    static {
        Font font;
        try {
            font = Font.createFont(
                    Font.TRUETYPE_FONT,
                    new File("fonts/StackSansText-Bold.ttf")
            ).deriveFont(12f);
        } catch (FontFormatException | IOException e) {
            font = new Font("Serif", Font.BOLD, 12);
        }

        BOLD_BODY_FONT = font;
    }

    public final static Font NORMAL_BODY_FONT;
    static {
        Font font;
        try {
            font = Font.createFont(
                    Font.TRUETYPE_FONT,
                    new File("fonts/StackSansText-Light.ttf")
            ).deriveFont(12f);
        } catch (FontFormatException | IOException e) {
            font = new Font("Serif", Font.PLAIN, 12);
        }

        NORMAL_BODY_FONT = font;
    }

}
