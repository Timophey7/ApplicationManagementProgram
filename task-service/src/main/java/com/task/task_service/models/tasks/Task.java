package com.task.task_service.models.tasks;

import com.task.task_service.models.app.App;
import com.task.task_service.models.enums.TaskCondition;
import com.task.task_service.models.enums.PriorityEnums;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(name = "app_tasks")
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    String taskName;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_id")
    App app;
    @Column(name = "app_unique_code")
    String appUniqueCode;
    @Column(name = "priority_enums")
    @Enumerated(EnumType.ORDINAL)
    PriorityEnums priorityEnums;
    @Column(name = "task_condition",nullable = false)
    @Enumerated(EnumType.STRING)
    TaskCondition condition;
    String description;
    LocalDateTime startTaskWork;
    LocalDateTime endTaskWork;
    String responsiblePerson;

}
