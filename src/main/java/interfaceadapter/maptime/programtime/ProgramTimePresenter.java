package interfaceadapter.maptime.programtime;

import usecase.maptime.UpdateMapTimeOutputData;
import usecase.maptime.UpdateMapTimeOutputBoundary;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/** Presenter class for UpdateMapTime interactor
 *
 */
public class ProgramTimePresenter implements UpdateMapTimeOutputBoundary {
    private final ProgramTimeViewModel programTimeViewModel;

    public ProgramTimePresenter(ProgramTimeViewModel programTimeViewModel) {
        this.programTimeViewModel = programTimeViewModel;
    }

    @Override
    public void updateTime(UpdateMapTimeOutputData newTime) {
        // update program time in programTimeViewModel
        ProgramTimeState programTimeState = programTimeViewModel.getState();
        programTimeState.setTime(formatTimeInstant(newTime.getStamp()));
        programTimeState.setSliderValue(converTimeToSlider(newTime.getStamp()));
        programTimeViewModel.firePropertyChange("time slider");
    }

    @Override
    public void updateTimeFromAnimator(UpdateMapTimeOutputData newTime) {
        ProgramTimeState programTimeState = programTimeViewModel.getState();
        programTimeState.setTime(formatTimeInstant(newTime.getStamp()));
        programTimeState.setSliderValue(converTimeToSlider(newTime.getStamp()));
        programTimeViewModel.firePropertyChange("animator");
    }

    private String formatTimeInstant(java.time.Instant instant) {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                .withZone(ZoneId.systemDefault())
                .format(instant);
    }

    private int converTimeToSlider(java.time.Instant instant) {
        java.time.Instant min = java.time.Instant.now();
        int max = 3*24; // TODO: don't hard code
        return (int)(java.time.Duration.between(min, instant).toHours()*100)/max;
    }
}
