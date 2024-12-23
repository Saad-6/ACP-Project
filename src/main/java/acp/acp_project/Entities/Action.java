package acp.acp_project.Entities;

import acp.acp_project.Models.SelectedFileAndAction;
import jakarta.persistence.*;

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

    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    public Action() {}

    public Action(String name, String outputFolderName, boolean isActive, SelectedFileAndAction selectedFileAndAction) {
        this.actionName = name;
        this.outputFolderName = outputFolderName;
        this.isActive = isActive;
        this.selectedFileAndAction = selectedFileAndAction;
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