package view;

import interfaceadapter.mapsettings.MapSettingsViewModel;
import interfaceadapter.mapsettings.MapSettingsViewModel.MapSettingsState;
import interfaceadapter.mapsettings.loadmapsettings.LoadMapSettingsController;
import interfaceadapter.mapsettings.savemapsettings.SaveMapSettingsController;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Swing-based view for displaying and editing persisted map settings.
 *
 * <p>The view observes a {@link MapSettingsViewModel} for changes and delegates
 * user actions to the load/save controllers.</p>
 */
public final class MapSettingsView extends JPanel implements PropertyChangeListener {

    private final MapSettingsViewModel viewModel;
    private final LoadMapSettingsController loadMapSettingsController;
    private final SaveMapSettingsController saveMapSettingsController;

    // UI components
    private final JTextField latitudeField = new JTextField(10);
    private final JTextField longitudeField = new JTextField(10);
    private final JSpinner zoomSpinner =
            new JSpinner(new SpinnerNumberModel(1, 0, 30, 1));

    private final JButton loadButton = new JButton("Load");
    private final JButton saveButton = new JButton("Save");

    private final JLabel statusLabel = new JLabel();
    private final JLabel errorLabel = new JLabel();

    /**
     * Creates a new map settings view.
     *
     * @param viewModel                 the map settings ViewModel
     * @param loadMapSettingsController controller for loading settings
     * @param saveMapSettingsController controller for saving settings
     */
    public MapSettingsView(MapSettingsViewModel viewModel,
                           LoadMapSettingsController loadMapSettingsController,
                           SaveMapSettingsController saveMapSettingsController) {

        this.viewModel = viewModel;
        this.loadMapSettingsController = loadMapSettingsController;
        this.saveMapSettingsController = saveMapSettingsController;

        setLayout(new BorderLayout());
        buildUi();

        // Listen to state changes from the view model.
        this.viewModel.addPropertyChangeListener(this);

        // Initialise from current state, if any.
        updateFromState(this.viewModel.getState());
    }

    /**
     * Builds the Swing component hierarchy and layout.
     */
    private void buildUi() {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Latitude
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Center latitude:"), gbc);

        gbc.gridx = 1;
        formPanel.add(latitudeField, gbc);

        row++;

        // Longitude
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Center longitude:"), gbc);

        gbc.gridx = 1;
        formPanel.add(longitudeField, gbc);

        row++;

        // Zoom
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Zoom level:"), gbc);

        gbc.gridx = 1;
        formPanel.add(zoomSpinner, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Buttons + status/error at the bottom.
        JPanel bottomPanel = new JPanel(new BorderLayout());

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonsPanel.add(loadButton);
        buttonsPanel.add(saveButton);

        bottomPanel.add(buttonsPanel, BorderLayout.NORTH);

        JPanel messagesPanel = new JPanel(new GridLayout(2, 1));
        statusLabel.setForeground(new Color(0, 128, 0)); // dark green
        errorLabel.setForeground(Color.RED);
        messagesPanel.add(statusLabel);
        messagesPanel.add(errorLabel);

        bottomPanel.add(messagesPanel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);

        hookUpActions();
    }

    /**
     * Wires UI actions to the controllers.
     */
    private void hookUpActions() {
        // Load existing settings.
        loadButton.addActionListener(e -> loadMapSettingsController.loadMapSettings());

        // Save current values from the fields.
        saveButton.addActionListener(e -> {
            String latText = latitudeField.getText().trim();
            String lonText = longitudeField.getText().trim();

            try {
                double latitude = Double.parseDouble(latText);
                double longitude = Double.parseDouble(lonText);
                int zoom = (Integer) zoomSpinner.getValue();

                saveMapSettingsController.saveMapSettings(latitude, longitude, zoom);
            } catch (NumberFormatException ex) {
                errorLabel.setText("Latitude and longitude must be valid numbers.");
            }
        });
    }

    /**
     * Reacts to changes in the {@link MapSettingsViewModel}.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (MapSettingsViewModel.STATE_PROPERTY.equals(evt.getPropertyName())
                || "state".equals(evt.getPropertyName())) {
            Object newValue = evt.getNewValue();
            if (newValue instanceof MapSettingsState) {
                updateFromState((MapSettingsState) newValue);
            }
        }
    }

    /**
     * Updates the UI components to reflect the provided state.
     */
    private void updateFromState(MapSettingsState state) {
        if (state == null) {
            latitudeField.setText("");
            longitudeField.setText("");
            zoomSpinner.setValue(1);
            statusLabel.setText("");
            errorLabel.setText("");
            return;
        }

        if (state.hasSavedSettings()) {
            latitudeField.setText(Double.toString(state.getCenterLatitude()));
            longitudeField.setText(Double.toString(state.getCenterLongitude()));
            zoomSpinner.setValue(state.getZoomLevel());
            statusLabel.setText("Saved map settings are available.");
        } else {
            statusLabel.setText("No saved map settings available yet.");
        }

        String error = state.getErrorMessage();
        errorLabel.setText(error == null ? "" : error);
    }
}
