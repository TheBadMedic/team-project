package interfaceadapter.maptime.programtime;

import interfaceadapter.ViewModel;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class ProgramTimeViewModel extends ViewModel<ProgramTimeState>{
    public static final String CURRENT_TIME_LABEL = "Current Time:";

    public ProgramTimeViewModel() {
        super("time slider");
        setState(new ProgramTimeState());
    }

    /**
     * Return a formatted string of the current time
     *
     * @return the formatted String with the current time in yyyy-MM-dd HH:mm form
     */
    public static String getCurrentTimeFormatted(){
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                    .withZone(ZoneId.systemDefault())
                    .format(java.time.LocalDateTime.now());
    }

}
