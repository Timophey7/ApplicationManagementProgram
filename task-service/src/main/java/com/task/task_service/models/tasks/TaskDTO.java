package com.task.task_service.models.tasks;

import com.task.task_service.models.enums.PriorityEnums;
import com.task.task_service.models.enums.TaskCondition;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TaskDTO {

    String taskName;
    PriorityEnums priorityEnums;
    TaskCondition condition;
    String description;
    LocalDateTime startTaskWork;
    LocalDateTime endTaskWork;
    String responsiblePerson;

}
