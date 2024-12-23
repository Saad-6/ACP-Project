package acp.acp_project;

import acp.acp_project.Entities.Action;
import acp.acp_project.Models.*;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

public class CreateActionDialog extends Dialog<Action> {
    private TextField nameField;
    private TextField outputFolderField;
    private ComboBox<String> fileTypeComboBox;
    private ComboBox<String> actionComboBox;

    public CreateActionDialog(String taskOutputPath) {
        setTitle("Create New Action");
        setHeaderText("Please enter the details for the new action.");

        ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        nameField = new TextField();
        nameField.setPromptText("Action Name");
        outputFolderField = new TextField();
        outputFolderField.setPromptText("Output Folder");
        outputFolderField.setText(taskOutputPath + "/[action-name]");

        fileTypeComboBox = new ComboBox<>();
        fileTypeComboBox.getItems().addAll("All Files", "JPEG", "PNG","JPG","ZIP", "Word", "PDF", "Text");
        fileTypeComboBox.setValue("All Files");
        styleComboBox(fileTypeComboBox);

        actionComboBox = new ComboBox<>();
        styleComboBox(actionComboBox);
        updateActionComboBox("All Files");

        fileTypeComboBox.setOnAction(e -> updateActionComboBox(fileTypeComboBox.getValue()));

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Output Folder:"), 0, 1);
        grid.add(outputFolderField, 1, 1);
        grid.add(new Label("File Type:"), 0, 2);
        grid.add(fileTypeComboBox, 1, 2);
        grid.add(new Label("Action:"), 0, 3);
        grid.add(actionComboBox, 1, 3);

        getDialogPane().setContent(grid);

        setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                return new Action(
                        nameField.getText(),
                        outputFolderField.getText(),
                        true,
                        new SelectedFileAndAction(fileTypeComboBox.getValue(), actionComboBox.getValue())
                );
            }
            return null;
        });
    }

    private void styleComboBox(ComboBox<String> comboBox) {
        comboBox.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #cccccc;" +
                        "-fx-border-width: 1px;" +
                        "-fx-border-radius: 3px;" +
                        "-fx-padding: 3px;"
        );

        comboBox.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                }
                //setBackground(javafx.scene.layout.Background(Color.WHITE));
            }
        });
    }

    private void updateActionComboBox(String fileType) {
        List<String> actions = new ArrayList<>(Arrays.stream(GenericActions.values())
                .map(Enum::name)
                .collect(Collectors.toList()));

        if (!fileType.equals("All Files")) {
            File selectedFile;
            switch (fileType) {
                case "JPEG":
                    selectedFile = new jpegFile();
                    break;
                case "PNG":
                    selectedFile = new pngFile();
                    break;
                case "JPG":
                    selectedFile = new jpgFile();
                    break;
                case "Word":
                    selectedFile = new wordFile();
                    break;
                case "PDF":
                    selectedFile = new pdfFile();
                    break;
                case "Text":
                    selectedFile = new textFile();
                    break;
                case "ZIP":
                    selectedFile = new zipFile();
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + fileType);
            }


            EnumSet<SpecificActions> specificActions = EnumSet.noneOf(SpecificActions.class);
            if (selectedFile instanceof pngFile) {
                specificActions = ((pngFile) selectedFile).specificActions;
            } else if (selectedFile instanceof wordFile) {
                specificActions = ((wordFile) selectedFile).specificActions;
            } else if (selectedFile instanceof pdfFile) {
                specificActions = ((pdfFile) selectedFile).specificActions;
            } else if (selectedFile instanceof textFile) {
                specificActions = ((textFile) selectedFile).specificActions;
            }

            actions.addAll(specificActions.stream()
                    .map(Enum::name)
                    .collect(Collectors.toList()));
        }

        actionComboBox.setItems(FXCollections.observableArrayList(actions));
        if (!actions.isEmpty()) {
            actionComboBox.setValue(actions.get(0));
        }
    }
}