package acp.acp_project.UI;

import acp.acp_project.Entities.HotFolder;
import acp.acp_project.Repository.GenericRepository;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;

import java.io.File;
import java.util.List;

import static acp.acp_project.UI.Utility.*;

public class HotFolderManagerDialog extends Dialog<Void> {
    private final GenericRepository<HotFolder> folderRepo;
    private ListView<HotFolder> folderListView;

    public HotFolderManagerDialog(Window owner, GenericRepository<HotFolder> folderRepo) {
        this.folderRepo = folderRepo;

        setTitle("Manage Hot Folders");
        setHeaderText("View, edit, or delete hot folders");
        initOwner(owner);

        ButtonType closeButtonType = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().addAll(closeButtonType);

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        folderListView = new ListView<>();
        folderListView.setCellFactory(this::createHotFolderCell);
        refreshFolderList();

        Button addButton = new Button("Add New Hot Folder");
        addButton.setOnAction(e -> addNewHotFolder());

        content.getChildren().addAll(folderListView, addButton);
        getDialogPane().setContent(content);

        setResultConverter(dialogButton -> null);
    }

    private void refreshFolderList() {
        List<HotFolder> folders = folderRepo.getAll();
        folderListView.getItems().setAll(folders);
    }

    private ListCell<HotFolder> createHotFolderCell(ListView<HotFolder> listView) {
        return new ListCell<>() {
            @Override
            protected void updateItem(HotFolder folder, boolean empty) {
                super.updateItem(folder, empty);
                if (empty || folder == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(10);
                    hbox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

                    Label pathLabel = new Label(folder.getPath());
                    HBox.setHgrow(pathLabel, Priority.ALWAYS);

                    Button editButton = createIconButton(EDIT_ICON, "Edit", "edit-btn");
                    editButton.setOnAction(e -> editHotFolder(folder));

                    Button deleteButton = createIconButton(DELETE_ICON, "Delete", "delete-btn");
                    deleteButton.setOnAction(e -> deleteHotFolder(folder));

                    hbox.getChildren().addAll(pathLabel, editButton, deleteButton);
                    setGraphic(hbox);
                }
            }
        };
    }

    private void addNewHotFolder() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Hot Folder");
        File selectedDirectory = directoryChooser.showDialog(getOwner());

        if (selectedDirectory != null) {
            HotFolder newFolder = new HotFolder();
            newFolder.setPath(selectedDirectory.getAbsolutePath());
            folderRepo.create(newFolder);
            refreshFolderList();
        }
    }

    private void editHotFolder(HotFolder folder) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select New Hot Folder Path");
        directoryChooser.setInitialDirectory(new File(folder.getPath()).getParentFile());
        File selectedDirectory = directoryChooser.showDialog(getOwner());

        if (selectedDirectory != null) {
            folder.setPath(selectedDirectory.getAbsolutePath());
            folderRepo.update(folder);
            refreshFolderList();
        }
    }

    private void deleteHotFolder(HotFolder folder) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirm Deletion");
        confirmDialog.setHeaderText("Delete Hot Folder");
        confirmDialog.setContentText("Are you sure you want to delete the hot folder: " + folder.getPath() + "?");

        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                folderRepo.delete(folder.getId());
                refreshFolderList();
            }
        });
    }
}