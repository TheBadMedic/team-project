package app;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        AppBuilder appBuilder = new AppBuilder();
        JFrame application = appBuilder
                .addUpdateOverlayUseCase()
                .createOverlayView()
                .addPanZoomView()
                .addMapOverlayView()
                .addLegendView()
                .addChangeOpacityView()
//                .addWeatherLayersUseCase()
                .addProgramTimeView()
                .addUpdateMapTimeUseCase()
//                .addBookmarkView()
//                .addInfoPanelView()
                .build();
        application.pack();
        application.setLocationRelativeTo(null);
        application.setVisible(true);
    }
}
