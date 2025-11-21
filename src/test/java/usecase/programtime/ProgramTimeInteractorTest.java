package usecase.programtime;

import entity.ProgramTime;

import usecase.maptime.UpdateMapTimeInputBoundary;
import usecase.maptime.UpdateMapTimeOutputBoundary;
import usecase.maptime.UpdateMapTimeOutputData;
import usecase.weatherLayers.update.UpdateOverlayOutputBoundary;
import usecase.weatherLayers.update.UpdateOverlaySizeInputBoundary;
import usecase.weatherLayers.update.UpdateOverlayUseCase;

import static org.mockito.Mockito.*;
import org.junit.jupiter.api.BeforeEach;


public class ProgramTimeInteractorTest {
    private ProgramTime programTime;
    private UpdateOverlaySizeInputBoundary updateOverlayUseCase;
    private UpdateMapTimeOutputBoundary updateMapTimeOutputBoundary;
    private UpdateMapTimeInputBoundary updateMapTimeInterator;

    @BeforeEach
    void setUp(){
        programTime = mock(ProgramTime.class);

    }
}
