package maptime;
import entity.ProgramTime;
import usecase.maptime.UpdateMapTimeOutputBoundary;
import usecase.maptime.UpdateMapTimeUseCase;
import usecase.weatherLayers.update.UpdateOverlayUseCase;

import static org.mockito.Mockito.mock;

public class UpdateMapTimeUseCaseTest {
    private ProgramTime programTime;
    private UpdateOverlayUseCase updateOverlayUseCase;
    private UpdateMapTimeUseCase updateMapTimeUseCase;
    private UpdateMapTimeOutputBoundary presenter;

    void setup(){
        programTime = mock(ProgramTime.class);
        updateOverlayUseCase = mock(UpdateOverlayUseCase.class);
        updateMapTimeUseCase = mock(UpdateMapTimeUseCase.class);
        presenter = mock(UpdateMapTimeOutputBoundary.class);\
    }
}
