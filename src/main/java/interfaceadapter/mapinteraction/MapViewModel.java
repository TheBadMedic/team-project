package interfaceadapter.mapinteraction;

import entity.Location;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;
public class MapViewModel {
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private Location center;
    private int zoomLevel;

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void updateFromViewport(int zoomLevel, double centerX, double centerY) {
        this.zoomLevel = zoomLevel;
        this.center = new Location(centerX, centerY);
        support.firePropertyChange("mapStateUpdated", null, this);
    }
    public Location getCenter() {
        return center;
    }
    public int getZoomLevel() {
        return zoomLevel;
    }
}