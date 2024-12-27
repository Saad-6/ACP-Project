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
    private final Task existingTask;

    public CreateTaskDialog(String hotFolder, Task task) {
        this.existingTask = task;
        setTitle(task == null ? "Create New Task" : "Edit Task");
        setHeaderText(null);

        ButtonType actionButtonType = new ButtonType(task == null ? "Create" : "Save", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(actionButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        hotFolderField.setText(hotFolder);
        hotFolderField.setEditable(false);
        hotFolderField.getStyleClass().add("read-only-field");

        if (task != null) {
            taskNameField.setText(task.getTaskName());
        }

        grid.add(new Label("Hot Folder:"), 0, 0);
        grid.add(hotFolderField, 1, 0);
        grid.add(new Label("Task Name:"), 0, 1);
        grid.add(taskNameField, 1, 1);

        getDialogPane().setContent(grid);

        Platform.runLater(() -> taskNameField.requestFocus());

        setResultConverter(dialogButton -> {
            if (dialogButton == actionButtonType) {
                if (existingTask == null) {
                    return new Task(taskNameField.getText(), false);
                } else {
                    existingTask.setTaskName(taskNameField.getText());
                    return existingTask;
                }
            }
            return null;
        });

        getDialogPane().getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        getDialogPane().getStyleClass().add("custom-dialog");
    }
}

