package acp.acp_project.UI;

import acp.acp_project.Entities.Action;
import acp.acp_project.Entities.HotFolder;
import acp.acp_project.Entities.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tooltip;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Optional;

public class Utility {
    public static final String FOLDER_ICON = "📁";
    public static final String ADD_ICON = "➕";
    public static final String PLAY_ICON = "▶";
    public static final String PAUSE_ICON = "⏸";
    public static final String SETTINGS_ICON = "⚙";
    public static final String DELETE_ICON = "🗑";
    public static final String EDIT_ICON = "✎";

    public static Button createIconButton(String unicode, String tooltip, String styleClass) {
        Button button = new Button(unicode);
        button.setTooltip(new Tooltip(tooltip));
        button.getStyleClass().add(styleClass);
        return button;
    }
    public static String selectFolder(Stage stage) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Hot Folder");

        // Show the directory chooser dialog
        File selectedDirectory = directoryChooser.showDialog(stage);

        // Return the absolute path if a directory was selected, otherwise return null
        if (selectedDirectory != null) {
            return selectedDirectory.getAbsolutePath();
        } else {
            return null; // No folder selected
        }
    }
    public static void showDeleteConfirmation(Object item, Runnable deleteTask, Runnable deleteAction) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Are you sure you want to delete this item?");
        alert.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (item instanceof Task) {
                deleteTask.run(); // Execute the Task deletion logic
            } else if (item instanceof Action) {
                deleteAction.run(); // Execute the Action deletion logic
            }
        }
    }
    public static void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Action Triggered");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
