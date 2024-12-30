package acp.acp_project;

import acp.acp_project.Entities.Action;
import acp.acp_project.Models.*;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.layout.VBox;

import java.util.*;
import java.util.stream.Collectors;

public class CreateActionDialog extends Dialog<Action> {
    private TextField nameField;
    private TextField outputFolderField;
    private ComboBox<String> fileTypeComboBox;
    private ComboBox<String> actionComboBox;
    private final Action existingAction;
    private GridPane grid;
    private Map<String, Control> dynamicFields;
    private ScrollPane scrollPane;

    public CreateActionDialog(String taskOutputPath, Action action) {
        this.existingAction = action;
        this.dynamicFields = new HashMap<>();

        setTitle(action == null ? "Create New Action" : "Edit Action");
        setHeaderText("Please enter the details for the action.");

        ButtonType actionButtonType = new ButtonType(action == null ? "Create" : "Save", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(actionButtonType, ButtonType.CANCEL);

        grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 10));

        scrollPane = new ScrollPane(grid);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        nameField = new TextField();
        nameField.setPromptText("Action Name");
        outputFolderField = new TextField();
        outputFolderField.setPromptText("Output Folder");
        outputFolderField.setText(taskOutputPath + "/[action-name]");

        fileTypeComboBox = new ComboBox<>();
        fileTypeComboBox.getItems().addAll("All Files", "jpeg", "png", "jpg", "zip", "docx", "pdf", "txt");
        fileTypeComboBox.setValue("All Files");
        styleComboBox(fileTypeComboBox);

        actionComboBox = new ComboBox<>();
        styleComboBox(actionComboBox);

        if (action != null) {
            nameField.setText(action.getActionName());
            outputFolderField.setText(action.getOutputFolder());
            fileTypeComboBox.setValue(action.selectedFileAndAction.selectedFileType);
            updateActionComboBox(action.selectedFileAndAction.selectedFileType);
            actionComboBox.setValue(action.selectedFileAndAction.selectedAction);
            updateDynamicFields(action.selectedFileAndAction.selectedAction);
            populateDynamicFields(action);
        } else {
            updateActionComboBox("All Files");
        }

        fileTypeComboBox.setOnAction(e -> updateActionComboBox(fileTypeComboBox.getValue()));
        actionComboBox.setOnAction(e -> updateDynamicFields(actionComboBox.getValue()));

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Output Folder:"), 0, 1);
        grid.add(outputFolderField, 1, 1);
        grid.add(new Label("File Type:"), 0, 2);
        grid.add(fileTypeComboBox, 1, 2);
        grid.add(new Label("Action:"), 0, 3);
        grid.add(actionComboBox, 1, 3);

        VBox contentBox = new VBox(scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        getDialogPane().setContent(contentBox);

        getDialogPane().setMinHeight(300);
        getDialogPane().setMinWidth(400);

        loadStylesheet();

        setResultConverter(dialogButton -> {
            if (dialogButton == actionButtonType) {
                Action resultAction = existingAction == null ? new Action() : existingAction;
                resultAction.setActionName(nameField.getText());
                resultAction.setOutputFolder(outputFolderField.getText());
                resultAction.setIsActive(true);
                resultAction.selectedFileAndAction = new SelectedFileAndAction(fileTypeComboBox.getValue(), actionComboBox.getValue());

                for (Map.Entry<String, Control> entry : dynamicFields.entrySet()) {
                    String value = "";
                    if (entry.getValue() instanceof TextField) {
                        value = ((TextField) entry.getValue()).getText();
                    } else if (entry.getValue() instanceof ComboBox) {
                        value = ((ComboBox<?>) entry.getValue()).getValue().toString();
                    }
                    resultAction.setActionParameter(entry.getKey(), value);
                }

                return resultAction;
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
                    setTextFill(Color.BLACK);
                }
            }
        });

        comboBox.getStyleClass().add("custom-combo-box");
    }

    private void loadStylesheet() {
        Scene scene = getDialogPane().getScene();
        if (scene != null) {
            scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        }
    }

    private void updateActionComboBox(String fileType) {
        List<String> actions = new ArrayList<>(Arrays.stream(GenericActions.values())
                .map(Enum::name)
                .collect(Collectors.toList()));

        if (!fileType.equals("All Files")) {
            File selectedFile = null;
            switch (fileType) {
                case "jpeg":
                    selectedFile = new jpegFile();
                    break;
                case "jpg":
                    selectedFile = new jpgFile();
                    break;
                case "png":
                    selectedFile = new pngFile();
                    break;
                case "docx":
                    selectedFile = new wordFile();
                    break;
                case "pdf":
                    selectedFile = new pdfFile();
                    break;
                case "txt":
                    selectedFile = new textFile();
                    break;
                case "zip":
                    selectedFile = new zipFile();
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + fileType);
            }

            EnumSet<SpecificActions> specificActions = EnumSet.noneOf(SpecificActions.class);
            if (selectedFile instanceof pngFile) {
                specificActions = ((pngFile) selectedFile).specificActions;
            } else if (selectedFile instanceof jpgFile) {
                specificActions = ((jpgFile) selectedFile).specificActions;
            } else if (selectedFile instanceof jpegFile) {
                specificActions = ((jpegFile) selectedFile).specificActions;
            } else if (selectedFile instanceof wordFile) {
                specificActions = ((wordFile) selectedFile).specificActions;
            } else if (selectedFile instanceof pdfFile) {
                specificActions = ((pdfFile) selectedFile).specificActions;
            } else if (selectedFile instanceof textFile) {
                specificActions = ((textFile) selectedFile).specificActions;
            } else if (selectedFile instanceof zipFile) {
                specificActions = ((zipFile) selectedFile).specificActions;
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

    private void updateDynamicFields(String actionName) {
        // Clear existing dynamic fields
        for (Node node : new ArrayList<>(grid.getChildren())) {
            if (GridPane.getRowIndex(node) >= 4) {
                grid.getChildren().remove(node);
            }
        }
        dynamicFields.clear();

        // Add new dynamic fields based on the selected action
        int row = 4;
        switch (actionName) {
            case "RENAME":
                addTextField("Rename Pattern", row++);
                break;
            case "FIND_AND_REPLACE":
                addTextField("Find", row++);
                addTextField("Replace", row++);
                break;
            case "SEARCH_BY_KEYWORD":
                addTextField("Keyword", row++);
                break;
            case "PRINT":
                addComboBox("Print Quality", Arrays.asList("Draft", "Normal", "High"), row++);
                break;
            // Add more cases for other actions that require additional fields
        }

        // Adjust the dialog size after adding new fields
        getDialogPane().getScene().getWindow().sizeToScene();
    }

    private void addTextField(String label, int row) {
        Label fieldLabel = new Label(label + ":");
        TextField textField = new TextField();
        grid.add(fieldLabel, 0, row);
        grid.add(textField, 1, row);
        dynamicFields.put(label, textField);
    }

    private void addComboBox(String label, List<String> options, int row) {
        Label fieldLabel = new Label(label + ":");
        ComboBox<String> comboBox = new ComboBox<>(FXCollections.observableArrayList(options));
        grid.add(fieldLabel, 0, row);
        grid.add(comboBox, 1, row);
        dynamicFields.put(label, comboBox);
    }

    private void populateDynamicFields(Action action) {
        for (Map.Entry<String, String> entry : action.getActionParameters().entrySet()) {
            Control control = dynamicFields.get(entry.getKey());
            if (control instanceof TextField) {
                ((TextField) control).setText(entry.getValue());
            } else if (control instanceof ComboBox) {
                @SuppressWarnings("unchecked")
                ComboBox<String> comboBox = (ComboBox<String>) control;
                comboBox.setValue(entry.getValue());
            }
        }
    }
}