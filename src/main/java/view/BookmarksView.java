package view;

import entity.BookmarkedLocation;
import interfaceadapter.bookmark.BookmarksViewModel;
import interfaceadapter.bookmark.BookmarksViewModel.BookmarksState;
import interfaceadapter.bookmark.addbookmark.AddBookmarkController;
import interfaceadapter.bookmark.removebookmark.RemoveBookmarkController;
import interfaceadapter.bookmark.listbookmark.ListBookmarksController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

/**
 * Swing-based view for displaying and managing bookmarked locations.
 *
 * <p>This view listens to a {@link BookmarksViewModel} for state changes and
 * delegates user actions to the appropriate controllers for the add, remove,
 * and list–bookmarks use cases.</p>
 */
public final class BookmarksView extends JPanel implements PropertyChangeListener {

    private final transient AddBookmarkController addBookmarkController;
    private final transient RemoveBookmarkController removeBookmarkController;
    private final transient ListBookmarksController listBookmarksController;

    // UI components
    private final JTextField nameField = new JTextField(15);
    private final JTextField latitudeField = new JTextField(8);
    private final JTextField longitudeField = new JTextField(8);

    private final DefaultListModel<String> bookmarksListModel = new DefaultListModel<>();
    private final JList<String> bookmarksList = new JList<>(bookmarksListModel);

    private final JButton addButton = new JButton("Add");
    private final JButton removeButton = new JButton("Remove");
    private final JButton refreshButton = new JButton("Refresh");

    private final JLabel errorLabel = new JLabel();

    /**
     * Creates a new bookmarks view.
     *
     * @param viewModel                the bookmarks ViewModel to observe
     * @param addBookmarkController    controller for the add–bookmark use case
     * @param removeBookmarkController controller for the remove–bookmark use case
     * @param listBookmarksController  controller for the list–bookmarks use case
     */
    public BookmarksView(BookmarksViewModel viewModel,
                         AddBookmarkController addBookmarkController,
                         RemoveBookmarkController removeBookmarkController,
                         ListBookmarksController listBookmarksController) {

        this.addBookmarkController = addBookmarkController;
        this.removeBookmarkController = removeBookmarkController;
        this.listBookmarksController = listBookmarksController;

        setLayout(new BorderLayout());
        buildUi();

        // Listen for state changes.
        viewModel.addPropertyChangeListener(this);

        // Initialise from current state, if any.
        updateFromState(viewModel.getState());
    }

    /**
     * Builds the Swing UI components and layout.
     */
    private void buildUi() {
        // Top panel: inputs for a new / existing bookmark.
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("Lat:"));
        inputPanel.add(latitudeField);

        inputPanel.add(new JLabel("Lon:"));
        inputPanel.add(longitudeField);

        add(inputPanel, BorderLayout.NORTH);

        // Center: list of bookmarks.
        bookmarksList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(bookmarksList);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom: buttons + error label.
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        buttonsPanel.add(addButton);
        buttonsPanel.add(removeButton);
        buttonsPanel.add(refreshButton);

        bottomPanel.add(buttonsPanel, BorderLayout.NORTH);

        errorLabel.setForeground(Color.RED);
        bottomPanel.add(errorLabel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);

        // Wire button actions.
        hookUpActions();
    }

    /**
     * Wires the button actions to the controllers.
     */
    private void hookUpActions() {
        // Add bookmark.
        addButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String latText = latitudeField.getText().trim();
            String lonText = longitudeField.getText().trim();

            try {
                double latitude = Double.parseDouble(latText);
                double longitude = Double.parseDouble(lonText);
                addBookmarkController.addBookmark(name, latitude, longitude);
            } catch (NumberFormatException ex) {
                // Local validation error – show directly in the view.
                errorLabel.setText("Latitude and longitude must be valid numbers.");
            }
        });

        // Remove bookmark. If a list item is selected, remove that; otherwise
        // fall back to the values typed in the text fields.
        removeButton.addActionListener(e -> {
            String selected = bookmarksList.getSelectedValue();
            String name;
            double latitude;
            double longitude;

            if (selected != null) {
                // Expected format: "name (lat, lon)"
                int parenIndex = selected.indexOf('(');
                int commaIndex = selected.indexOf(',');
                int closeIndex = selected.indexOf(')');
                if (parenIndex > 0 && commaIndex > parenIndex && closeIndex > commaIndex) {
                    name = selected.substring(0, parenIndex).trim();
                    String latText = selected.substring(parenIndex + 1, commaIndex).trim();
                    String lonText = selected.substring(commaIndex + 1, closeIndex).trim();
                    try {
                        latitude = Double.parseDouble(latText);
                        longitude = Double.parseDouble(lonText);
                    } catch (NumberFormatException ex) {
                        // Fall back to text fields if parsing fails.
                        handleRemoveUsingFields();
                        return;
                    }
                } else {
                    // Unexpected format; fall back to fields.
                    handleRemoveUsingFields();
                    return;
                }
            } else {
                // No selected item; use fields.
                handleRemoveUsingFields();
                return;
            }

            removeBookmarkController.removeBookmark(name, latitude, longitude);
        });

        // Refresh / list bookmarks.
        refreshButton.addActionListener(e -> listBookmarksController.listBookmarks());
    }

    /**
     * Helper used by the remove button when it needs to rely on the text fields.
     */
    private void handleRemoveUsingFields() {
        String name = nameField.getText().trim();
        String latText = latitudeField.getText().trim();
        String lonText = longitudeField.getText().trim();
        try {
            double latitude = Double.parseDouble(latText);
            double longitude = Double.parseDouble(lonText);
            removeBookmarkController.removeBookmark(name, latitude, longitude);
        } catch (NumberFormatException ex) {
            errorLabel.setText("Latitude and longitude must be valid numbers.");
        }
    }

    /**
     * Reacts to changes in the {@link BookmarksViewModel} state.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (BookmarksViewModel.STATE_PROPERTY.equals(evt.getPropertyName())
                || "state".equals(evt.getPropertyName())) {
            Object newValue = evt.getNewValue();
            if (newValue instanceof BookmarksState) {
                updateFromState((BookmarksState) newValue);
            }
        }
    }

    /**
     * Updates the Swing components to reflect the given state.
     */
    private void updateFromState(BookmarksState state) {
        if (state == null) {
            bookmarksListModel.clear();
            errorLabel.setText("");
            return;
        }

        // Update list contents.
        bookmarksListModel.clear();
        List<BookmarkedLocation> bookmarks = state.getBookmarks();
        if (bookmarks != null) {
            for (BookmarkedLocation b : bookmarks) {
                String entry = String.format("%s (%.6f, %.6f)",
                        b.getName(), b.getLatitude(), b.getLongitude());
                bookmarksListModel.addElement(entry);
            }
        }

        // Update error label.
        String error = state.getErrorMessage();
        errorLabel.setText(error == null ? "" : error);
    }
}
