package usecase.maptime;

public interface UpdateMapTimeOutputBoundary {

    /** Update the program time label WITHOUT updating the slider based
     * off this new time
     *
     * @param time the time to update the time displayed in the UI
     */
    void updateTime(UpdateMapTimeOutputData time);

    /** Update the program time label AND update the slider based off this
     * new time
     *
     * @param time the time to update the time displayed in the UI
     */
    void updateTimeFromAnimator(UpdateMapTimeOutputData time);
}
