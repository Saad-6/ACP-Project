package acp.acp_project.Code;

import acp.acp_project.Entities.HotFolder;
import acp.acp_project.Repository.GenericRepository;
import acp.acp_project.UI.Utility;
import javafx.stage.Stage;

public class HotFolderManager {
    private final GenericRepository<HotFolder> folderRepo = new GenericRepository<>(HotFolder.class);

    public HotFolder AddHotFolder(){

        String path = Utility.selectFolder(new Stage());
        HotFolder folder = new HotFolder();
        folder.setPath(path);

        return folderRepo.create(folder);

    }
}
