package interfaceadapter.infopanel;

import usecase.infopanel.InfoPanelInputBoundary;
import usecase.infopanel.InfoPanelInputData;

import javax.swing.*;

public final class InfoPanelController {
    private final InfoPanelInputBoundary interactor;
    private final Timer debounce;

    private double pendingLat;
    private double pendingLon;
    private int pendingZoom;

    public InfoPanelController(InfoPanelInputBoundary interactor) {
        this.interactor = interactor;
        this.debounce = new Timer(300, e -> fire());
        this.debounce.setRepeats(false);
    }

    public void onViewportChanged(double centerLat, double centerLon, int zoom) {
        this.pendingLat  = centerLat;
        this.pendingLon  = centerLon;
        this.pendingZoom = zoom;
        debounce.restart();
    }

    private void fire() {
        interactor.execute(new InfoPanelInputData(pendingLat, pendingLon, pendingZoom));
    }
}
