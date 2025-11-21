package usecase.maptime;

public class TickMapTimeInputData {
    private final int ticks;

    public TickMapTimeInputData(int ticks){
        if (ticks <= 0){
            throw new IllegalArgumentException("Ticks must be non-negative integers.");
        }
        this.ticks = ticks;
    }

    public int getTicks() {
        return ticks;
    }
}
