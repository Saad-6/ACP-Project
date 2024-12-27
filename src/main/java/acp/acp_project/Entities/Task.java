package acp.acp_project.Entities;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String taskName;

    private boolean isActive;

    @ManyToOne
    @JoinColumn(name = "hot_folder_id", nullable = false)
    private HotFolder hotFolder;

    @OneToMany(mappedBy = "task", orphanRemoval = true,fetch = FetchType.EAGER)
    private List<Action> actions = new ArrayList<>();

    public Task() {}

    public Task(String name, boolean isActive) {
        this.taskName = name;
        this.isActive = isActive;
    }

    public int getId() {
        return id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public HotFolder getHotFolder() {
        return hotFolder;
    }

    public void setHotFolder(HotFolder hotFolder) {
        this.hotFolder = hotFolder;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void addAction(Action action) {
        action.setTask(this);
        this.actions.add(action);
    }

    public void removeAction(Action action) {
        actions.remove(action);
        action.setTask(null);
    }

    public String getStatus() {
        return isActive ? "Active" : "Paused";
    }

    public void toggleStatus() {
        isActive = !isActive;
    }
}