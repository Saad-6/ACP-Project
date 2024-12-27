package acp.acp_project.Code;

import acp.acp_project.Domain.Response;
import acp.acp_project.Entities.Action;
import acp.acp_project.Entities.Task;
import acp.acp_project.Repository.GenericRepository;

import java.util.stream.Collectors;

public class TaskManager {

    private final GenericRepository<Task> taskRepo = new GenericRepository<>(Task.class);
    private ActionManager actionManager = new ActionManager();
    Response response =  new Response();
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
    public Response runTask(Task task){

        var activeActions =  task.getActions().stream()
                .filter(Action::getIsActive)
                .collect(Collectors.toList());

        for(Action action : activeActions){
            response = actionManager.runAction(action);
            if(!response.success){
                return response;
            }

        }
        return response;
    }

}

