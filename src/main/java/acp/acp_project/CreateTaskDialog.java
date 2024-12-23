package acp.acp_project;

import acp.acp_project.Entities.Task;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class CreateTaskDialog extends Dialog<Task> {
    private final TextField taskNameField = new TextField();
    private final TextField hotFolderField = new TextField();
    private final List<TextField> actionFields = new ArrayList<>();
    private final VBox actionsContainer = new VBox(10);

    public CreateTaskDialog(String hotFolder) {
        setTitle("Create New Task");
        setHeaderText(null);

        // Set the button types
        ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        // Create the content grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        hotFolderField.setText(hotFolder);
        hotFolderField.setEditable(false);
        hotFolderField.getStyleClass().add("read-only-field");

        grid.add(new Label("Hot Folder:"), 0, 0);
        grid.add(hotFolderField, 1, 0);
        grid.add(new Label("Task Name:"), 0, 1);
        grid.add(taskNameField, 1, 1);

        getDialogPane().setContent(grid);

        // Request focus on the task name field by default
        Platform.runLater(() -> taskNameField.requestFocus());

        // Convert the result to a Task object when the create button is clicked
        setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                Task newTask = new Task(taskNameField.getText(), false);

                return newTask;
            }
            return null;
        });

        // Apply custom styles
        getDialogPane().getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        getDialogPane().getStyleClass().add("custom-dialog");
    }

}

