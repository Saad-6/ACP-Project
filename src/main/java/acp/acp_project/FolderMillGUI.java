package acp.acp_project;

import acp.acp_project.Code.ActionManager;
import acp.acp_project.Code.HotFolderManager;
import acp.acp_project.Code.TaskManager;
import acp.acp_project.Domain.Response;
import acp.acp_project.Entities.HotFolder;
import acp.acp_project.Entities.Task;
import acp.acp_project.Entities.Action;
import acp.acp_project.Repository.GenericRepository;
import acp.acp_project.UI.HotFolderManagerDialog;
import acp.acp_project.UI.Utility;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.scene.text.Text;
import java.util.List;
import static acp.acp_project.UI.Utility.*;

public class FolderMillGUI extends Application {

    // Repos
    private final GenericRepository<HotFolder> folderRepo = new GenericRepository<>(HotFolder.class);
    private final GenericRepository<Task> taskRepo = new GenericRepository<>(Task.class);
    private final GenericRepository<Action> actionRepo = new GenericRepository<>(Action.class);

    // Entity lists
    private ObservableList<HotFolder> hotFolders = FXCollections.observableArrayList();
    private ObservableList<Task> tasks = FXCollections.observableArrayList();
    private ObservableList<Action> actions = FXCollections.observableArrayList();

    // List Views
    private ListView<HotFolder> hotFoldersListView;
    private ListView<Task> tasksListView;
    private ListView<Action> actionsListView;

    // Helper Classes
    HotFolderManager folderManager = new HotFolderManager();
    ActionManager actionManager = new ActionManager();
    TaskManager taskManager = new TaskManager();


    // UI Classes


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        folderRepo.initializeDatabase();
        primaryStage.setTitle("Folder Mill");

        BorderPane root = new BorderPane();
        root.getStyleClass().add("light-gray-bg");
        root.setTop(createToolbar(primaryStage));
        root.setLeft(createHotFoldersPanel());
        root.setCenter(createTasksPanel());
        root.setRight(createActionsPanel());

        Scene scene = new Scene(root, 1000, 600);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();

        loadRealData();
    }


    private VBox createHotFoldersPanel() {
        VBox panel = new VBox();
        panel.getStyleClass().addAll("panel", "panel-white");

        HBox header = new HBox();
        header.getStyleClass().add("section-header");
        Label title = new Label("Hot Folders");
        Button addButton = createIconButton(ADD_ICON, "Add Folder", "toggle-button-default");
        addButton.setOnAction(e -> selectFolder());
        HBox.setHgrow(title, Priority.ALWAYS);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getChildren().addAll(title, addButton);

        hotFoldersListView = new ListView<>(hotFolders);
        hotFoldersListView.setPrefWidth(200);
        hotFoldersListView.getStyleClass().add("hot-folders-list");
        hotFoldersListView.setCellFactory(createHotFolderCellFactory());

        hotFoldersListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                updateTasksList(newSelection);
            } else {
                tasks.clear();
            }
            tasksListView.refresh();
            actions.clear();
            actionsListView.refresh();
        });

        panel.getChildren().addAll(header, hotFoldersListView);
        return panel;
    }

    private void selectFolder() {

        hotFolders.add(folderManager.AddHotFolder());
        hotFoldersListView.refresh();

    }

    private Callback<ListView<HotFolder>, ListCell<HotFolder>> createHotFolderCellFactory() {
        return listView -> new ListCell<HotFolder>() {
            @Override
            protected void updateItem(HotFolder folder, boolean empty) {
                super.updateItem(folder, empty);
                if (empty || folder == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(10);
                    hbox.setAlignment(Pos.CENTER_LEFT);
                    Text icon = new Text(FOLDER_ICON);
                    icon.getStyleClass().add("folder-icon");
                    Label nameLabel = new Label(folder.getPath());
                    nameLabel.getStyleClass().add("folder-name");
                    hbox.getChildren().addAll(icon, nameLabel);
                    setGraphic(hbox);
                }
            }
        };
    }

    private VBox createTasksPanel() {
        VBox panel = new VBox(10);
        panel.getStyleClass().addAll("panel", "panel-white");
        panel.setPrefWidth(300);

        HBox header = new HBox();
        header.getStyleClass().add("section-header");
        Label title = new Label("Tasks");
        Button addButton = createIconButton(ADD_ICON, "Add Task", "toggle-button-default");
        addButton.setOnAction(e -> showCreateTaskDialog());
        HBox.setHgrow(title, Priority.ALWAYS);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getChildren().addAll(title, addButton);

        tasksListView = new ListView<>(tasks);
        tasksListView.setCellFactory(createTaskCellFactory());

        tasksListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                updateActionsListView(newSelection);
            } else {
                actions.clear();
            }
            actionsListView.refresh();
        });

        panel.getChildren().addAll(header, tasksListView);
        return panel;
    }

    private void showHotFolderManagerDialog(Stage primaryStage) {
        HotFolderManagerDialog dialog = new HotFolderManagerDialog(primaryStage, folderRepo);
        dialog.showAndWait();
        loadRealData(); // Refresh the main UI after managing hot folders
    }


    private ToolBar createToolbar(Stage primaryStage) {
        Button addFolderBtn = createIconButton(FOLDER_ICON, "Add Folder", "icon-button-light");
        Button addTaskBtn = createIconButton(ADD_ICON, "Add Task", "icon-button-light");
        Button startBtn = createIconButton(PLAY_ICON, "Start/Pause", "icon-button-light");
        Button settingsBtn = createIconButton(SETTINGS_ICON, "Settings", "icon-button-light");


        // Event Handlers
        addFolderBtn.setOnAction(e -> selectFolder());
        // Add new Task Button
        addTaskBtn.setOnAction(e -> showCreateTaskDialog());
        // Start a Task
        startBtn.setOnAction(e -> toggleSelectedTask());
        //Settings
        settingsBtn.setOnAction(e -> showHotFolderManagerDialog(primaryStage));


        ToolBar toolBar = new ToolBar(addFolderBtn, addTaskBtn, startBtn, settingsBtn);
        toolBar.getStyleClass().add("custom-toolbar");
        return toolBar;
    }

    private void toggleSelectedTask() {
        Task selectedTask = tasksListView.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            showAlert("Please select a task first.");
            return;
        }

        Response response = taskManager.runTask(selectedTask);
        if (response.success) {
            selectedTask.toggleStatus();
            taskRepo.update(selectedTask);
            tasksListView.refresh();
            showAlert(response.Message);
        } else {
            showAlert("Failed to run task: " + response.Message);
        }
    }

    private Callback<ListView<Task>, ListCell<Task>> createTaskCellFactory() {
        return listView -> new ListCell<Task>() {
            @Override
            protected void updateItem(Task task, boolean empty) {
                super.updateItem(task, empty);
                if (empty || task == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox vbox = new VBox(5);
                    vbox.getStyleClass().add("task-cell");

                    HBox header = new HBox(10);
                    header.setAlignment(Pos.CENTER_LEFT);
                    Label nameLabel = new Label(task.getTaskName());
                    nameLabel.getStyleClass().add("task-name");
                    Label statusLabel = new Label(task.getStatus());
                    statusLabel.getStyleClass().add("task-status");
                    Button editButton = createIconButton(EDIT_ICON, "Edit", "edit-btn");
                    editButton.setOnAction(e -> showEditTaskDialog(task));
                    Button deleteButton = createIconButton(DELETE_ICON, "Delete", "delete-btn");
                    deleteButton.setOnAction(e -> showDeleteConfirmation(task));

                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    header.getChildren().addAll(nameLabel, statusLabel, spacer, editButton, deleteButton);

                    vbox.getChildren().add(header);
                    setGraphic(vbox);
                }
            }
        };
    }


    private VBox createActionsPanel() {
        VBox panel = new VBox(10);
        panel.getStyleClass().addAll("panel", "panel-white");

        HBox header = new HBox();
        header.getStyleClass().add("section-header");
        Label title = new Label("Actions");
        Button addButton = createIconButton(ADD_ICON, "Add Action", "toggle-button-default");
        addButton.setOnAction(e -> showCreateActionDialog());
        HBox.setHgrow(title, Priority.ALWAYS);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getChildren().addAll(title, addButton);

        actionsListView = new ListView<>(actions);
        actionsListView.setPrefWidth(250);
        actionsListView.setCellFactory(createActionCellFactory());

        panel.getChildren().addAll(header, actionsListView);
        return panel;
    }

    private void toggleActionStatus(Action action) {
        action.toggleStatus();
        actionRepo.update(action);
        Task selectedTask = tasksListView.getSelectionModel().getSelectedItem();
        updateActionsListView(selectedTask);
    }

    private Callback<ListView<Action>, ListCell<Action>> createActionCellFactory() {
        return listView -> new ListCell<Action>() {
            @Override
            protected void updateItem(Action action, boolean empty) {
                super.updateItem(action, empty);
                if (empty || action == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(10);
                    hbox.setAlignment(Pos.CENTER_LEFT);
                    hbox.getStyleClass().add("action-cell");

                    Label nameLabel = new Label(action.getActionName());
                    nameLabel.getStyleClass().add("action-name");
                    Label jobLabel = new Label(action.selectedFileAndAction.selectedAction);
                    jobLabel.getStyleClass().add("action-job");
                    Button toggleBtn = createIconButton(
                            action.getStatus().equals("Active") ? PAUSE_ICON : PLAY_ICON,
                            action.getStatus().equals("Active") ? "Pause" : "Start",
                            "icon-button-cells"
                    );

                    // Toggle pause/unpause
                    toggleBtn.setOnAction(e -> toggleActionStatus(action));

                    Button editButton = createIconButton(EDIT_ICON, "Edit", "edit-btn");
                    editButton.setOnAction(e -> showEditActionDialog(action));
                    Button deleteButton = createIconButton(DELETE_ICON, "Delete", "delete-btn");
                    deleteButton.setOnAction(e -> showDeleteConfirmation(action));

                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    hbox.getChildren().addAll(nameLabel, jobLabel, spacer, toggleBtn, editButton, deleteButton);
                    setGraphic(hbox);
                }
            }
        };
    }
    private void showEditTaskDialog(Task task) {
        HotFolder selectedHotFolder = hotFoldersListView.getSelectionModel().getSelectedItem();
        if (selectedHotFolder == null) {
            showAlert("Error: Hot Folder not selected.");
            return;
        }

        CreateTaskDialog dialog = new CreateTaskDialog(selectedHotFolder.getPath(), task);
        dialog.initOwner(tasksListView.getScene().getWindow());
        dialog.showAndWait().ifPresent(updatedTask -> {
            try {
                taskRepo.update(updatedTask);
                int index = tasks.indexOf(task);
                if (index != -1) {
                    tasks.set(index, updatedTask);
                }
                tasksListView.refresh();
                showAlert("Task updated successfully: " + updatedTask.getTaskName());
            } catch (Exception e) {
                showAlert("Failed to update task: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private void showEditActionDialog(Action action) {
        Task selectedTask = tasksListView.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            showAlert("Error: Task not selected.");
            return;
        }

        CreateActionDialog dialog = new CreateActionDialog(selectedTask.getHotFolder().getPath(), action);
        dialog.initOwner(actionsListView.getScene().getWindow());
        dialog.showAndWait().ifPresent(updatedAction -> {
            try {
                actionRepo.update(updatedAction);
                int index = actions.indexOf(action);
                if (index != -1) {
                    actions.set(index, updatedAction);
                }
                actionsListView.refresh();
                showAlert("Action updated successfully: " + updatedAction.getActionName());
            } catch (Exception e) {
                showAlert("Failed to update action: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private void updateTasksList(HotFolder selectedHotFolder) {
        if (selectedHotFolder != null) {
            List<Task> filteredTasks = taskRepo.getByHotFolder(selectedHotFolder.getId());
            tasks.setAll(filteredTasks);
        } else {
            tasks.clear();
        }
    }

    private void updateActionsListView(Task selectedTask) {
        if (selectedTask != null) {
            List<Action> filteredActions = actionRepo.getByTask(selectedTask.getId());
            actions.setAll(filteredActions);
        } else {
            actions.clear();
        }
    }

    private void loadRealData() {
        List<HotFolder> allHotFolders = folderRepo.getAll();
        hotFolders.setAll(allHotFolders);
    }

    private void showCreateTaskDialog() {
        HotFolder selectedHotFolder = hotFoldersListView.getSelectionModel().getSelectedItem();
        if (selectedHotFolder == null) {
            showAlert("Please select a Hot Folder first.");
            return;
        }

        CreateTaskDialog dialog = new CreateTaskDialog(selectedHotFolder.getPath(),null);
        dialog.initOwner(hotFoldersListView.getScene().getWindow());
        dialog.showAndWait().ifPresent(task -> {
            task.setHotFolder(selectedHotFolder);
            try {
                Task createdTask = taskRepo.create(task);
                tasks.add(createdTask);
                tasksListView.refresh();
                showAlert("Task created successfully: " + createdTask.getTaskName());
            } catch (Exception e) {
                showAlert("Failed to create task: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private void showCreateActionDialog() {
        Task selectedTask = tasksListView.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            showAlert("Please select a Task first.");
            return;
        }
        CreateActionDialog dialog = new CreateActionDialog(selectedTask.getHotFolder().getPath(),null);
        dialog.initOwner(hotFoldersListView.getScene().getWindow());
        dialog.showAndWait().ifPresent(action -> {
            action.setTask(selectedTask);
            try {
                Action createdAction = actionRepo.create(action);
                selectedTask.addAction(createdAction);
                updateActionsListView(selectedTask);
                tasksListView.refresh();
                showAlert("Action created successfully: " + createdAction.getActionName());
            } catch (Exception e) {
                showAlert("Failed to create action: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private void showDeleteConfirmation(Object item) {

        Task task;
        Action action;
        if(item instanceof Task){
            task = (Task)item;
            Utility.showDeleteConfirmation(item, () -> deleteTask(task), () -> deleteAction(new Action()));

        }else if(item instanceof Action){
            action = (Action)item;
            Utility.showDeleteConfirmation(item, () -> deleteTask(new Task()), () -> deleteAction(action));
        }
    }

    private void deleteTask(Task task) {
        var response = taskManager.delete(task);
        if(response.success){
            showAlert("Task deleted successfully: " + task.getTaskName());
            tasks.remove(task);
            tasksListView.refresh();
        }
        else{
            showAlert("Failed to delete task: " + response.Message);
        }
    }

    private void deleteAction(Action action) {
        var response = actionManager.delete(action);
        if(response.success){
            showAlert("Action deleted successfully: " + action.getActionName());
            actions.remove(action);
            actionsListView.refresh();
        }
        else{
            showAlert("Failed to delete action: " + response.Message );
        }
    }
}