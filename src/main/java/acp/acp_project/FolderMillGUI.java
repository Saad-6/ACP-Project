package acp.acp_project;

import acp.acp_project.Entities.HotFolder;
import acp.acp_project.Entities.Task;
import acp.acp_project.Entities.Action;
import acp.acp_project.Repository.GenericRepository;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.scene.text.Text;

import java.io.File;
import java.util.List;
import java.util.Optional;

public class FolderMillGUI extends Application {

    // Unicode symbols for icons
    private static final String FOLDER_ICON = "📁";
    private static final String ADD_ICON = "➕";
    private static final String PLAY_ICON = "▶";
    private static final String PAUSE_ICON = "⏸";
    private static final String SETTINGS_ICON = "⚙";
    private static final String DELETE_ICON = "🗑";
    private static final String EDIT_ICON = "✎";

    // Repos
    private final GenericRepository<HotFolder> folderRepo = new GenericRepository<>(HotFolder.class);
    private final GenericRepository<Task> taskRepo = new GenericRepository<>(Task.class);
    private final GenericRepository<Action> actionRepo = new GenericRepository<>(Action.class);

    private ObservableList<HotFolder> hotFolders = FXCollections.observableArrayList();
    private ObservableList<Task> tasks = FXCollections.observableArrayList();
    private ObservableList<Action> actions = FXCollections.observableArrayList();
    private ListView<HotFolder> hotFoldersListView;
    private ListView<Task> tasksListView;
    private ListView<Action> actionsListView;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        folderRepo.initializeDatabase();
        primaryStage.setTitle("Folder Mill");

        BorderPane root = new BorderPane();
        root.getStyleClass().add("light-gray-bg");
        root.setTop(createToolbar());
        root.setLeft(createHotFoldersPanel());
        root.setCenter(createTasksPanel());
        root.setRight(createActionsPanel());

        Scene scene = new Scene(root, 1000, 600);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();

        loadRealData();
    }

    private Button createIconButton(String unicode, String tooltip, String styleClass) {
        Button button = new Button(unicode);
        button.setTooltip(new Tooltip(tooltip));
        button.getStyleClass().add(styleClass);
        return button;
    }

    private ToolBar createToolbar() {
        Button addFolderBtn = createIconButton(FOLDER_ICON, "Add Folder", "icon-button-light");
        Button addTaskBtn = createIconButton(ADD_ICON, "Add Task", "icon-button-light");
        Button startBtn = createIconButton(PLAY_ICON, "Start", "icon-button-light");
        Button pauseBtn = createIconButton(PAUSE_ICON, "Pause", "icon-button-light");
        Button settingsBtn = createIconButton(SETTINGS_ICON, "Settings", "icon-button-light");
        TextField searchField = new TextField();
        searchField.setPromptText("Search...");

        addFolderBtn.setOnAction(e -> selectFolder());
        addTaskBtn.setOnAction(e -> showCreateTaskDialog());
        startBtn.setOnAction(e -> showAlert("Start clicked"));
        pauseBtn.setOnAction(e -> showAlert("Pause clicked"));
        settingsBtn.setOnAction(e -> showAlert("Settings clicked"));
        searchField.setOnAction(e -> showAlert("Search: " + searchField.getText()));

        ToolBar toolBar = new ToolBar(addFolderBtn, addTaskBtn, startBtn, pauseBtn, settingsBtn, searchField);
        toolBar.getStyleClass().add("custom-toolbar");
        return toolBar;
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
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Hot Folder");

        File selectedDirectory = directoryChooser.showDialog(hotFoldersListView.getScene().getWindow());

        if (selectedDirectory != null) {
            String folderPath = selectedDirectory.getAbsolutePath();

            if (selectedDirectory.exists() && selectedDirectory.isDirectory()) {
                HotFolder newHotFolder = new HotFolder();
                newHotFolder.setPath(folderPath);

                try {
                    HotFolder savedHotFolder = folderRepo.create(newHotFolder);
                    hotFolders.add(savedHotFolder);
                    showAlert("The folder has been successfully added: " + folderPath);
                } catch (Exception e) {
                    showAlert("Failed to add the hot folder: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                showAlert("The selected folder does not exist or is not a directory.");
            }
        }
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
                    Button toggleBtn = createIconButton(
                            task.getStatus().equals("Active") ? PAUSE_ICON : PLAY_ICON,
                            task.getStatus().equals("Active") ? "Pause" : "Start",
                            "icon-button-cells"
                    );
                    toggleBtn.setOnAction(e -> {
                        task.toggleStatus();
                        taskRepo.update(task);
                        updateItem(task, false);
                    });
                    Button editButton = createIconButton(EDIT_ICON, "Edit", "edit-btn");
                    Button deleteButton = createIconButton(DELETE_ICON, "Delete", "delete-btn");
                    deleteButton.setOnAction(e -> showDeleteConfirmation(task));

                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    header.getChildren().addAll(nameLabel, statusLabel, spacer, toggleBtn, editButton, deleteButton);

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
                    toggleBtn.setOnAction(e -> {
                        action.toggleStatus();
                        actionRepo.update(action);
                        updateItem(action, false);
                    });
                    Button editButton = createIconButton(EDIT_ICON, "Edit", "edit-btn");
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

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Action Triggered");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showCreateTaskDialog() {
        HotFolder selectedHotFolder = hotFoldersListView.getSelectionModel().getSelectedItem();
        if (selectedHotFolder == null) {
            showAlert("Please select a Hot Folder first.");
            return;
        }

        CreateTaskDialog dialog = new CreateTaskDialog(selectedHotFolder.getPath());
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
        CreateActionDialog dialog = new CreateActionDialog(selectedTask.getHotFolder().getPath());
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
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Are you sure you want to delete this item?");
        alert.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (item instanceof Task) {
                deleteTask((Task) item);
            } else if (item instanceof Action) {
                deleteAction((Action) item);
            }
        }
    }

    private void deleteTask(Task task) {
        try {
            taskRepo.delete(task.getId());
            tasks.remove(task);
            tasksListView.refresh();
            showAlert("Task deleted successfully: " + task.getTaskName());
        } catch (Exception e) {
            showAlert("Failed to delete task: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void deleteAction(Action action) {
        try {
            actionRepo.delete(action.getId());
            Task parentTask = action.getTask();
            parentTask.removeAction(action);
            updateActionsListView(parentTask);
            tasksListView.refresh();
            showAlert("Action deleted successfully: " + action.getActionName());
        } catch (Exception e) {
            showAlert("Failed to delete action: " + e.getMessage());
            e.printStackTrace();
        }
    }
}