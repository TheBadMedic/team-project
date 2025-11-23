package java.maptime;
import entity.ProgramTime;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import usecase.maptime.*;
import usecase.weatherLayers.update.UpdateOverlayInputBoundary;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class UpdateMapTimeUseCaseTest {

    @Test
    public void checkCorrectManualUpdateOfProgramTime() {
        java.time.Instant mockTime = LocalDateTime.of(
                2025,
                11,
                22,
                0,
                0
        ).toInstant(ZoneOffset.UTC);

        ProgramTime programTime = new ProgramTime(mockTime);
        UpdateOverlayInputBoundary updateOverlayUseCase = mock(UpdateOverlayInputBoundary.class);
        UpdateMapTimeOutputBoundary programTimePresenter = mock(UpdateMapTimeOutputBoundary.class);

        UpdateMapTimeUseCase updateMapTimeUseCase = new UpdateMapTimeUseCase(
                programTime,
                updateOverlayUseCase,
                programTimePresenter
        );

        UpdateMapTimeInputData updateMapTimeInputData = new UpdateMapTimeInputData(mockTime);

        updateMapTimeUseCase.execute(updateMapTimeInputData);

        verify(updateOverlayUseCase).update();

        ArgumentCaptor<UpdateMapTimeOutputData> updateMapTimeOutputDataArgumentCaptor =
                ArgumentCaptor.forClass(UpdateMapTimeOutputData.class);
        verify(programTimePresenter).updateTime(updateMapTimeOutputDataArgumentCaptor.capture());

        UpdateMapTimeOutputData updateMapTimeOutputData = updateMapTimeOutputDataArgumentCaptor.getValue();
        assertEquals(mockTime, updateMapTimeOutputData.getStamp());

    }

    @Test
    public void checkIncrementingProgramTime(){
        java.time.Instant currentTime = Instant.now();
        ProgramTime programTime = new ProgramTime(currentTime);
        UpdateOverlayInputBoundary updateOverlayUseCase = mock(UpdateOverlayInputBoundary.class);
        UpdateMapTimeOutputBoundary programTimePresenter = mock(UpdateMapTimeOutputBoundary.class);

        UpdateMapTimeInputBoundary updateMapTimeUseCase = new UpdateMapTimeUseCase(
                programTime,
                updateOverlayUseCase,
                programTimePresenter
        );

        int ticks = 3;

        TickMapTimeInputData tickMapTimeInputData = new TickMapTimeInputData(ticks);

        updateMapTimeUseCase.execute(tickMapTimeInputData);

        verify(updateOverlayUseCase).update();

        ArgumentCaptor<UpdateMapTimeOutputData> updateMapTimeOutputDataArgumentCaptor =
                ArgumentCaptor.forClass(UpdateMapTimeOutputData.class);
        verify(programTimePresenter).updateTimeFromAnimator(updateMapTimeOutputDataArgumentCaptor.capture());

        UpdateMapTimeOutputData updateMapTimeOutputData = updateMapTimeOutputDataArgumentCaptor.getValue();
        assertEquals(
                currentTime.plus(ProgramTime.TIME_INCREMENT.multipliedBy(ticks)),
                updateMapTimeOutputData.getStamp()
        );

    }

    @Test
    public void checkOverflowOfTimeFromIncrement() {

        java.time.Instant currentTime = Instant.now();
        ProgramTime programTime = new ProgramTime(currentTime.plus(ProgramTime.TIME_INCREMENT.multipliedBy(72)));
        UpdateOverlayInputBoundary updateOverlayUseCase = mock(UpdateOverlayInputBoundary.class);
        UpdateMapTimeOutputBoundary programTimePresenter = mock(UpdateMapTimeOutputBoundary.class);

        UpdateMapTimeInputBoundary updateMapTimeUseCase = new UpdateMapTimeUseCase(
                programTime,
                updateOverlayUseCase,
                programTimePresenter
        );

        int ticks = 1;

        TickMapTimeInputData tickMapTimeInputData = new TickMapTimeInputData(ticks);

        updateMapTimeUseCase.execute(tickMapTimeInputData);

        verify(updateOverlayUseCase).update();

        ArgumentCaptor<UpdateMapTimeOutputData> updateMapTimeOutputDataArgumentCaptor =
                ArgumentCaptor.forClass(UpdateMapTimeOutputData.class);
        verify(programTimePresenter).updateTimeFromAnimator(updateMapTimeOutputDataArgumentCaptor.capture());

        UpdateMapTimeOutputData updateMapTimeOutputData = updateMapTimeOutputDataArgumentCaptor.getValue();
        assertEquals(
                currentTime.truncatedTo(ChronoUnit.MINUTES),
                updateMapTimeOutputData.getStamp().truncatedTo(ChronoUnit.MINUTES)
        );

    }


}
