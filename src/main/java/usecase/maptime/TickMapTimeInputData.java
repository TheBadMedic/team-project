package usecase.maptime;

public class TickMapTimeInputData {
    private final int ticks;

    public TickMapTimeInputData(int ticks){
        if (ticks <=0){
            throw new IndexOutOfBoundsException();
        }
        this.ticks = ticks;
    }

    public int getTicks() {
        return ticks;
    }
}
