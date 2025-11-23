package usecase.maptime;

import entity.ProgramTime;
import usecase.weatherLayers.update.UpdateOverlayUseCase;

public class UpdateMapTimeUseCase implements UpdateMapTimeInputBoundary {
    private final ProgramTime programTime;
    private final UpdateOverlayUseCase updateOverlayUseCase;
    private final UpdateMapTimeOutputBoundary programTimePresenter;

    public UpdateMapTimeUseCase(ProgramTime programTime, UpdateOverlayUseCase updateOverlayUseCase, UpdateMapTimeOutputBoundary programTimePresenter) {
        this.programTime = programTime;
        this.updateOverlayUseCase = updateOverlayUseCase;
        this.programTimePresenter = programTimePresenter;
    }

    @Override
    public void setProgramTime(UpdateMapTimeInputData updateMapTimeInputData) {
        programTime.setTime(updateMapTimeInputData.getCurrentTime());
        updateOverlayUseCase.update();
        UpdateMapTimeOutputData outputData = new UpdateMapTimeOutputData(
                updateMapTimeInputData.getCurrentTime()
        );
        programTimePresenter.updateTime(
                outputData
                );
    }

    @Override
    public void incrementProgramTimePerTick(TickMapTimeInputData ticks){
        programTime.incrementTime();
        updateOverlayUseCase.update();

        UpdateMapTimeOutputData outputData = new UpdateMapTimeOutputData(
                programTime.getCurrentTime().plus(ProgramTime.TIME_INCREMENT.multipliedBy(ticks.getTicks()))
        );
        programTimePresenter.updateTimeFromAnimator(
            outputData
        );

    }
}
