package com.task.task_service.models.app;

import com.task.task_service.models.change.Change;
import com.task.task_service.models.tasks.Task;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(name = "apps")
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class App {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    @Column(name = "app_name")
    String name;
    String gitHubUserName;
    @Column(name = "app_unique_code")
    String uniqueCode;
    String description;
    @OneToMany(mappedBy = "app")
    List<Task> tasks = new ArrayList<>();
    @OneToMany(mappedBy = "app")
    List<Change> changes = new ArrayList<>();

    public void addNewTask(Task task){
        tasks.add(task);
    }

    public void AddNewChange(Change change){
        changes.add(change);
    }
}
