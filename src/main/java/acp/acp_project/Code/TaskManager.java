package acp.acp_project.Code;

import acp.acp_project.Entities.Task;
import acp.acp_project.Repository.GenericRepository;

public class TaskManager {

    private final GenericRepository<Task> taskRepo = new GenericRepository<>(Task.class);

    public boolean delete(Task task){
        try {
            taskRepo.delete(task.getId());
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}

