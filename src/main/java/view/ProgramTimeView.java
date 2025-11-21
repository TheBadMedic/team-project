package view;

import app.uielements.PauseIcon;
import app.uielements.PlayIcon;
import constants.Constants;
import interfaceadapter.maptime.programtime.ProgramTimeController;
import interfaceadapter.maptime.programtime.ProgramTimeState;
import interfaceadapter.maptime.programtime.ProgramTimeViewModel;
import interfaceadapter.maptime.timeanimation.TimeAnimationController;
import uielements.CustomSliderUI;
import constants.DefaultThemes;
import java.awt.event.ActionEvent;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * The View for the Program Time slider
 */
public class ProgramTimeView extends JPanel implements PropertyChangeListener {

    private final String viewName = "program time";
    private final ProgramTimeViewModel programTimeViewModel;
    private ProgramTimeController programTimeController;
    private TimeAnimationController timeAnimationController;
    private final JSlider timeSlider;
    private final JButton playPauseButton;
    private final JLabel currentTimeTitleLabel;
    private final JLabel currentTime;

    public ProgramTimeView(ProgramTimeViewModel programTimeViewModel) {
        this.programTimeViewModel = programTimeViewModel;
        programTimeViewModel.addPropertyChangeListener(this);
        this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

        timeSlider = new JSlider(SwingConstants.HORIZONTAL, 0, 100, 0);
        timeSlider.setUI(new CustomSliderUI(timeSlider));
        timeSlider.setPaintTicks(false);
        timeSlider.addChangeListener(
                evt -> {
                    JSlider source = (JSlider) evt.getSource();
                    if (source.getValueIsAdjusting()) {
                        this.programTimeController.updateTime(source.getValue());
                    }
                }
        );
        timeSlider.setOpaque(false);
        timeSlider.setPaintTicks(false);
        timeSlider.setPreferredSize(new Dimension(Constants.SLIDER_WIDTH, Constants.SLIDER_HEIGHT));

        playPauseButton = new JButton(new PlayIcon(Constants.PLAY_PAUSE_BUTTON_HEIGHT,
                Constants.PLAY_PAUSE_BUTTON_WIDTH, Color.ORANGE));

        playPauseButton.addActionListener((ActionEvent e1) -> {
            Icon current = playPauseButton.getIcon();
            if (current instanceof PlayIcon ) {
                playPauseButton.setIcon(new PauseIcon(Constants.PLAY_PAUSE_BUTTON_WIDTH,
                        Constants.PLAY_PAUSE_BUTTON_HEIGHT, Color.ORANGE));
                timeAnimationController.play();
            }
            else{
                playPauseButton.setIcon(new PlayIcon(Constants.PLAY_PAUSE_BUTTON_HEIGHT,
                        Constants.PLAY_PAUSE_BUTTON_WIDTH, Color.ORANGE));
                timeAnimationController.pause();
            }
        });

        currentTimeTitleLabel = new JLabel(ProgramTimeViewModel.CURRENT_TIME_LABEL);
        currentTimeTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        currentTime = new JLabel(ProgramTimeViewModel.getCurrentTimeFormatted());
        currentTime.setFont(DefaultThemes.NORMAL_BODY_FONT);
        currentTimeTitleLabel.setFont(DefaultThemes.BOLD_BODY_FONT);
        this.add(playPauseButton);
        this.add(timeSlider);
        this.add(currentTimeTitleLabel);
        this.add(currentTime);
    }

    public String getViewName() {
        return viewName;
    }
    public void setProgramTimeController(ProgramTimeController controller) {
        this.programTimeController = controller;
    }

    public void setTimeAnimationController(TimeAnimationController controller) {
        this.timeAnimationController = controller;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("time slider")) {
            final ProgramTimeState state = (ProgramTimeState) evt.getNewValue();
            currentTime.setText(state.getTime());
        }
        else if (evt.getPropertyName().equals("animator")) {
            final ProgramTimeState state = (ProgramTimeState) evt.getNewValue();
            timeSlider.setValue(state.getSliderValue());
            currentTime.setText(state.getTime());
        }
    }
}
