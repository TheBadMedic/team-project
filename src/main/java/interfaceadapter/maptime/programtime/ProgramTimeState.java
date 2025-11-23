package interfaceadapter.maptime.programtime;

public class ProgramTimeState {
    private String time;
    private int sliderValue;

    public String getTime() {
        return time;
    }

    public int getSliderValue(){ return sliderValue;}

    public void setTime(String time) {
        this.time = time;
    }

    public void setSliderValue(int sliderValue) { this.sliderValue = sliderValue; }
}
