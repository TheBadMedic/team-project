package uielements;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.*;

public class CustomSliderUI extends BasicSliderUI {
    private static final int DEFAULT_TRACK_HEIGHT = 15;
    private static final int DEFAULT_TRACK_OFFSET = 20;
    private static final Color DEFAULT_START_COLOUR = Color.BLUE;
    private static final Color DEFAULT_END_COLOUR = Color.ORANGE;
    private static final Color DEFAULT_ARROW_COLOUR = Color.PINK;
    private static final int DEFAULT_ARC_RADIUS = 20;
    private static final int DEFAULT_ARROW_WIDTH = 12;
    private static final int DEFAULT_ARROW_HEIGHT = 10;


    public CustomSliderUI(JSlider slider) {
        super(slider);
    }

    @Override
    public void paintTrack(Graphics g){
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        GradientPaint gradientPaint =
                new GradientPaint(
                        trackRect.x,
                        0 ,
                        DEFAULT_START_COLOUR,
                        trackRect.x + trackRect.width,
                        0,
                        DEFAULT_END_COLOUR
                );
        g2d.setPaint(gradientPaint);
        g2d.fillRoundRect(
                trackRect.x,
                trackRect.y + DEFAULT_TRACK_OFFSET,
                trackRect.width,
                DEFAULT_TRACK_HEIGHT,
                DEFAULT_ARC_RADIUS,
                DEFAULT_ARC_RADIUS
        );
        g2d.dispose();
    }

    @Override
    public void paintThumb(Graphics g){

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setComposite(AlphaComposite.Src);
        g2d.setColor(new Color(0, 0, 0, 0));
        Polygon triangle = getPolygon();
        g2d.setColor(DEFAULT_ARROW_COLOUR);
        g2d.fill(triangle);
        g2d.draw(triangle);
        g2d.dispose();
    }

    @NotNull
    private Polygon getPolygon() {
        int defaultThumbCentreX = thumbRect.x + thumbRect.width / 2;
        int defaultThumbCentreY = thumbRect.y + thumbRect.height / 2;
        int arrowOffset = defaultThumbCentreY - DEFAULT_ARROW_HEIGHT * 2;
        Polygon triangle = new Polygon();
        triangle.addPoint(defaultThumbCentreX, arrowOffset + DEFAULT_ARROW_HEIGHT);
        triangle.addPoint(defaultThumbCentreX - DEFAULT_ARROW_WIDTH/2, arrowOffset);
        triangle.addPoint(defaultThumbCentreX + DEFAULT_ARROW_WIDTH/2, arrowOffset);
        return triangle;
    }

    @Override
    protected Dimension getThumbSize() {
        return new Dimension(20,50);
    }
    @Override
    public void paintFocus(Graphics g){
        // intentionally empty
    }

}
