package usecase.maptime;

public interface UpdateMapTimeInputBoundary {

    /**
     * Set the program time to the passed in time
     * @param updateMapTimeInputData the time to set program time to
     */
    void setProgramTime(UpdateMapTimeInputData updateMapTimeInputData);

    /**
     * Update the program time by one hour for each tick
     * @param ticks number of ticks to progress time by, one hour per tick
     */
    void incrementProgramTimePerTick(TickMapTimeInputData ticks);

}
