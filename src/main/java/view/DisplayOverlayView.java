package view;

import constants.Constants;
import interfaceadapter.weatherLayers.UpdateOverlayController;
import interfaceadapter.weatherLayers.UpdateOverlaySizeController;
import interfaceadapter.weatherLayers.UpdateOverlayViewModel;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Objects;

public class DisplayOverlayView extends JPanel implements PropertyChangeListener {
    private final UpdateOverlayViewModel view;
    private final UpdateOverlaySizeController sizeController;
    private final ImageIcon imageIcon;

    public DisplayOverlayView(UpdateOverlaySizeController sizeCont,
                              UpdateOverlayViewModel vm){
        view = vm;
        view.addPropertyChangeListener(this);

        sizeController = sizeCont;

        imageIcon = new ImageIcon();
        JLabel label = new JLabel();
        label.setIcon(imageIcon);
        this.setBounds(0,0, Constants.DEFAULT_MAP_WIDTH, Constants.DEFAULT_MAP_HEIGHT);
        this.setBackground(new Color(0,0,0,0));
        this.setOpaque(false);
        this.add(label);

    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        //Listener listening to size change in MapOverlayStructureView and listening to viewmodel's change.
        if(Objects.equals(evt.getPropertyName(), "size")) {
            this.setBounds(new Rectangle((Dimension) evt.getNewValue()));
            sizeController.changeSize((Dimension) evt.getNewValue());
        } else { //overlay update
            imageIcon.setImage(view.getState().getImage());
            this.repaint();
        }
    }


}
