package acp.acp_project.Code;

import acp.acp_project.Entities.Action;
import acp.acp_project.Entities.Task;
import acp.acp_project.Repository.GenericRepository;

public class ActionManager {
    private final GenericRepository<Action> actionRepo = new GenericRepository<>(Action.class);
    
    public boolean delete(Action action){
     try {
        actionRepo.delete(action.getId());
        Task parentTask = action.getTask();
        parentTask.removeAction(action);
      return  true;
    } catch (Exception e) {

        e.printStackTrace();
        return false;
    }

    }

}
