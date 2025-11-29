package view;

import constants.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;

public class MapOverlayStructureView extends JLayeredPane{
    private final PropertyChangeSupport support;
    private Dimension size;
    private Timer resizeTimer;

    public MapOverlayStructureView(){
        this.setPreferredSize(new Dimension(Constants.DEFAULT_MAP_WIDTH, Constants.DEFAULT_MAP_HEIGHT));
        size = this.getSize();

        //Use a timer to prevent overlay from trying to update every frame to reduce update lag
        resizeTimer = new Timer(15, e -> {
            resizeTimer.stop();
            this.fireSizeChange();
        });

        //Components in JLayeredPane do not automatically resize, and requires property change listener method
        support = new PropertyChangeSupport(this);
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (resizeTimer.isRunning()) {
                    resizeTimer.restart();
                } else {
                    resizeTimer.start();
                }
            }
        });

    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void fireSizeChange(){
            Dimension newSize = this.getSize();
            support.firePropertyChange("size", size, newSize);
            size = newSize;
    }

    public void addComponent(JPanel component, int layer){
        this.add(component, new Integer(layer));
        //new Integer object required as specified in docs
        //https://docs.oracle.com/javase/8/docs/api/javax/swing/JLayeredPane.html
    }

}
