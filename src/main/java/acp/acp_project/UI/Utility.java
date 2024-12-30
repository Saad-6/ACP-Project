package acp.acp_project.UI;

import acp.acp_project.Domain.Response;
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
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
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
    // Helper method to extract the file name without the extension
    public static String getFileNameWithoutExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex > 0) ? fileName.substring(0, dotIndex) : fileName;
    }

    // Helper method to extract the file extension
    public static String getFileExtension(File file) {
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex > 0) ? fileName.substring(dotIndex + 1) : "";
    }

    public static List<File> prepareFiles(Action action){

        String fileType = action.selectedFileAndAction.selectedFileType;
        fileType = getExtensionFromFileName(fileType);
        File rootDirectory = new File(action.getTask().getHotFolder().getPath());
        if(rootDirectory == null){
           return  new ArrayList<>();
        }

        List<File> filesToBeSentToTheGulag = findFiles(rootDirectory,fileType);
        return filesToBeSentToTheGulag;
    }
    public static void ensureDirectoryExists(String destinationPath){
        File destinationDirectory = new File(destinationPath);
        if (!destinationDirectory.exists()) {
            boolean created = destinationDirectory.mkdirs(); // Create the directory and any necessary parent directories
            if (created) {
                System.out.println("Directory created: " + destinationPath);
            } else {
                System.err.println("Failed to create directory: " + destinationPath);
            }
        }
    }
    public static List<File> findFiles(File directory, String fileType) {
        List<File> matchingFiles = new ArrayList<>();
        File[] files = directory.listFiles();

        if (files == null) {
            return matchingFiles;
        }

        if (!"all".equals(fileType)) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith("." + fileType)) {
                    matchingFiles.add(file);
                } else if (file.isDirectory()) {
                    matchingFiles.addAll(findFiles(file, fileType)); // Recursively search subdirectories
                }
            }
        } else {
            for (File file : files) {
                if (file.isFile()) {
                    matchingFiles.add(file);
                } else if (file.isDirectory()) {
                    matchingFiles.addAll(findFiles(file, fileType)); // Recursively add all files in subdirectories
                }
            }
        }

        return matchingFiles;
    }
    public static String getExtensionFromFileName(String fileName) {
        switch (fileName.toLowerCase()) {
            case "jpeg":
                return fileName.toLowerCase();
            case "jpg":
                return fileName.toLowerCase();
            case "png":
                return fileName.toLowerCase();
            case "zip":
                return fileName.toLowerCase();
            case "word":
                return "docx";
            case "pdf":
                return "pdf";
            case "text":
                return "txt";
            case "txt":
                return fileName.toLowerCase();
            default:
                return "all";
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
