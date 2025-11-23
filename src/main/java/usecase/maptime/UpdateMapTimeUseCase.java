package usecase.maptime;

import entity.ProgramTime;
import usecase.weatherLayers.update.UpdateOverlayInputBoundary;
import usecase.weatherLayers.update.UpdateOverlaySizeInputBoundary;
import usecase.weatherLayers.update.UpdateOverlayUseCase;

public class UpdateMapTimeUseCase implements UpdateMapTimeInputBoundary {
    private final ProgramTime programTime;
    private final UpdateOverlayInputBoundary updateOverlayUseCase;
    private final UpdateMapTimeOutputBoundary programTimePresenter;

    public UpdateMapTimeUseCase(ProgramTime programTime, UpdateOverlayInputBoundary updateOverlayUseCase, UpdateMapTimeOutputBoundary programTimePresenter) {
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
        for(int i = 0; i < ticks.getTicks(); i++){
            programTime.incrementTime();
        }
        updateOverlayUseCase.update();

        UpdateMapTimeOutputData outputData = new UpdateMapTimeOutputData(
                programTime.getCurrentTime());

        programTimePresenter.updateTimeFromAnimator(
            outputData
        );

    }
}
