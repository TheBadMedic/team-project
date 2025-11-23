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

    public MapOverlayStructureView(){

        this.setPreferredSize(new Dimension(Constants.DEFAULT_MAP_WIDTH, Constants.DEFAULT_MAP_HEIGHT));

        //Components in JLayeredPane do not automatically resize, and requires an observer method
        support = new PropertyChangeSupport(this);
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) { //TODO fix or remove after JMV
//                Dimension newSize = e.getComponent().getSize();
//                support.firePropertyChange("size", size, newSize);
//                size = newSize;
            }
        });

    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void addComponent(JPanel component, int layer){
        this.add(component, new Integer(layer));
        // new Integer object required as specified in docs
        // https://docs.oracle.com/javase/8/docs/api/javax/swing/JLayeredPane.html
    }

}
