package usecase.maptime;


import entity.ProgramTime;
import interfaceadapter.maptime.ProgramTimePresenter;
import usecase.UpdateOverlayUseCase;

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
    public void execute(UpdateMapTimeInputData updateMapTimeInputData) {
        programTime.setTime(updateMapTimeInputData.getCurrentTime());
//        updateOverlayUseCase.update();
        programTimePresenter.updateTime(
                new UpdateMapTimeOutputData(
                        updateMapTimeInputData.getCurrentTime()
                ));
    }
}
