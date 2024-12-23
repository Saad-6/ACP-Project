package acp.acp_project.Entities;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class HotFolder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String path;

    @OneToMany(mappedBy = "hotFolder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks = new ArrayList<>();

    public int getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void addTask(Task task) {
        task.setHotFolder(this);
        this.tasks.add(task);
    }

    public void removeTask(Task task) {
        tasks.remove(task);
        task.setHotFolder(null);
    }
}