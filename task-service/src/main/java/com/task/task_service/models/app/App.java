package com.task.task_service.models.app;

import com.task.task_service.models.change.Change;
import com.task.task_service.models.tasks.Task;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "apps",indexes = {
        @Index(columnList = "name"),
        @Index(columnList = "uniqueCode")
})
public class App implements Serializable {

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

}
