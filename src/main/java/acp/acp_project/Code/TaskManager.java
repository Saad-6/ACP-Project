package acp.acp_project.Code;

import acp.acp_project.Domain.Response;
import acp.acp_project.Entities.Action;
import acp.acp_project.Entities.Task;
import acp.acp_project.Repository.GenericRepository;

import java.util.List;
import java.util.stream.Collectors;

public class TaskManager {

    private final GenericRepository<Task> taskRepo = new GenericRepository<>(Task.class);
    private ActionManager actionManager = new ActionManager();
    Response response =  new Response();
    private final GenericRepository<Action> actionRepo = new GenericRepository<>(Action.class);
    public Response delete(Task task){

        try {
            taskRepo.delete(task.getId());
            return response;

        } catch (Exception e) {
            e.printStackTrace();
            response.success = false;
            response.Message = e.getMessage();
            return response;
        }
    }
    public Response runTask(Task task) {

        Response response = new Response();
        List<Action> actions = actionRepo.getByTask(task.getId());

        for (Action action : actions) {
            // Refresh the action from the database
            action = actionRepo.getById(action.getId());
            if (action.getIsActive()) {
                response = actionManager.runAction(action);
                if (!response.success) {
                    return response;
                }
            }
        }
        return response;
    }

}

