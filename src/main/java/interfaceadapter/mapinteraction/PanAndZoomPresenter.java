package interfaceadapter.mapinteraction;

import entity.Viewport;
import usecase.mapinteraction.PanAndZoomOutputBoundary;
import usecase.mapinteraction.PanAndZoomOutputData;
import org.openstreetmap.gui.jmapviewer.JMapViewer;

import java.awt.*;

public class PanAndZoomPresenter implements PanAndZoomOutputBoundary {

    private final Viewport viewport;
    private final JMapViewer mapViewer;
    private final MapViewModel mapViewModel;

    public PanAndZoomPresenter(Viewport viewport,
                               JMapViewer mapViewer,
                               MapViewModel mapViewModel) {
        this.viewport = viewport;
        this.mapViewer = mapViewer;
        this.mapViewModel = mapViewModel;
    }


    @Override
    public void present(PanAndZoomOutputData outputData) {
        Viewport updatedViewport = outputData.getViewport();
        mapViewModel.updateFromViewport(
                updatedViewport.getZoomLevel(),
                updatedViewport.getPixelCenterX(),
                updatedViewport.getPixelCenterY()
        );
        int centerX = (int) updatedViewport.getPixelCenterX();
        int centerY = (int) updatedViewport.getPixelCenterY();
        mapViewer.setCenter(new Point(centerX, centerY));
        mapViewer.setZoom(updatedViewport.getZoomLevel());
    }
}

