package view;

import constants.Constants;
import interfaceadapter.weatherlayers.updateoverlay.UpdateOverlaySizeController;
import interfaceadapter.weatherlayers.updateoverlay.UpdateOverlayViewModel;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Objects;

public class DisplayOverlayView extends JPanel implements PropertyChangeListener {
    private final transient UpdateOverlayViewModel view;
    private final transient UpdateOverlaySizeController sizeController;
    private final ImageIcon imageIcon;

    public DisplayOverlayView(UpdateOverlaySizeController sizeCont,
                              UpdateOverlayViewModel vm){
        this.setOpaque(false);
        this.setEnabled(false);
        setFocusable(false);
        view = vm;
        view.addPropertyChangeListener(this);

        sizeController = sizeCont;

        imageIcon = new ImageIcon();
        JLabel label = new JLabel();
        label.setIcon(imageIcon);
        this.add(label);
        this.setBackground(new Color(0,0,0,0));
        this.setBounds(0,0, Constants.DEFAULT_MAP_WIDTH, Constants.DEFAULT_MAP_HEIGHT);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        //Listener listening to size change in MapOverlayStructureView and listening to viewmodel's change.
        if(Objects.equals(evt.getPropertyName(), "size")) {
            Rectangle rect = new Rectangle((Dimension) evt.getNewValue());
            //five pixel offset needed for proper alignment
            this.setBounds(0, rect.y - 5, rect.width, rect.height - 5);
            this.setSize((Dimension)evt.getNewValue());
            sizeController.changeSize((Dimension) evt.getNewValue());
        } else { //overlay update
            imageIcon.setImage(view.getState().getImage());
        }
        this.repaint();
    }


}
