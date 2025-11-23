package view;

import constants.Constants;
import interfaceadapter.weatherLayers.LegendViewModel;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class LegendsView extends JPanel implements PropertyChangeListener {
    private final ImageIcon imageIcon;
    private final transient LegendViewModel legendView;

    public LegendsView(LegendViewModel legendView){
        this.legendView = legendView;
        legendView.addPropertyChangeListener(this);

        imageIcon = new ImageIcon();
        JLabel label = new JLabel();
        label.setIcon(imageIcon);
        this.add(label);
    }
//this.setBounds((int)(Constants.DEFAULT_MAP_WIDTH * 0.5), (int)(Constants.DEFAULT_MAP_HEIGHT * 0.8),
//    Constants.DEFAULT_MAP_WIDTH, Constants.DEFAULT_MAP_HEIGHT);
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        imageIcon.setImage(legendView.getState().getImage().getScaledInstance(Constants.DEFAULT_PROGRAM_WIDTH ,-1,
                Image.SCALE_REPLICATE));
        this.repaint();
    }
}
