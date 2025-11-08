package interfaceadapter.maptime;

import interfaceadapter.ViewModel;
import interfaceadapter.maptime.ProgramTimeState;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class ProgramTimeViewModel extends ViewModel<ProgramTimeState> {
    public static final String CURRENT_TIME_LABEL = "Current Time:";

    public ProgramTimeViewModel() {
        super("time slider");
        setState(new ProgramTimeState());
    }

    public static String getCurrentTimeFormatted(){
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    .withZone(ZoneId.systemDefault())
                    .format(java.time.LocalDateTime.now());
    }

}
