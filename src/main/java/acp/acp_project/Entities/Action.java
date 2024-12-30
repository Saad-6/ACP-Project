package acp.acp_project.Entities;

import acp.acp_project.Models.SelectedFileAndAction;
import jakarta.persistence.*;

import java.util.HashMap;
import java.util.Map;

@Entity
public class Action {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String actionName;
    @Column(nullable = false)
    public String outputFolderName;
    private Boolean isActive;

    @Embedded
    public SelectedFileAndAction selectedFileAndAction;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "action_parameters", joinColumns = @JoinColumn(name = "action_id"))
    @MapKeyColumn(name = "parameter_name")
    @Column(name = "parameter_value")
    private Map<String, String> actionParameters;

    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    public Action() {
        this.actionParameters = new HashMap<>();
    }

    public Action(String name, String outputFolderName, boolean isActive, SelectedFileAndAction selectedFileAndAction) {
        this.actionName = name;
        this.outputFolderName = outputFolderName;
        this.isActive = isActive;
        this.selectedFileAndAction = selectedFileAndAction;
    }

    public void setActionParameter(String key, String value) {
        this.actionParameters.put(key, value);
    }

    public String getActionParameter(String key) {
        return this.actionParameters.get(key);
    }

    public Map<String, String> getActionParameters() {
        return this.actionParameters;
    }

    public int getId() {
        return id;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Task getTask() {
        return task;
    }
public String getOutputFolder(){
        return  outputFolderName;
}
public void setOutputFolder(String newFolderName){
        this.outputFolderName = newFolderName;
}
    public void setTask(Task task) {
        this.task = task;
    }

    public String getStatus() {
        return isActive ? "Active" : "Paused";
    }

    public void toggleStatus() {
        isActive = !isActive;
    }
}