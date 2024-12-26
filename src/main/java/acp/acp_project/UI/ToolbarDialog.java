package acp.acp_project.UI;

import acp.acp_project.Code.HotFolderManager;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;

import static acp.acp_project.UI.Utility.*;

public class ToolbarDialog {

    private final HotFolderManager folderManager = new HotFolderManager();

    public ToolBar createToolbar() {

        Button addFolderBtn = createIconButton(FOLDER_ICON, "Add Folder", "icon-button-light");
        Button addTaskBtn = createIconButton(ADD_ICON, "Add Task", "icon-button-light");
        Button startBtn = createIconButton(PLAY_ICON, "Start", "icon-button-light");
        Button pauseBtn = createIconButton(PAUSE_ICON, "Pause", "icon-button-light");
        Button settingsBtn = createIconButton(SETTINGS_ICON, "Settings", "icon-button-light");
        TextField searchField = new TextField();
        searchField.setPromptText("Search...");

        addFolderBtn.setOnAction(e -> folderManager.AddHotFolder());
        addTaskBtn.setOnAction(e ->  non());
        startBtn.setOnAction(e -> non());
        pauseBtn.setOnAction(e -> non());
        settingsBtn.setOnAction(e -> non());
        searchField.setOnAction(e -> non());

        ToolBar toolBar = new ToolBar(addFolderBtn, addTaskBtn, startBtn, pauseBtn, settingsBtn, searchField);
        toolBar.getStyleClass().add("custom-toolbar");
        return toolBar;
    }
    void non(){}
}
