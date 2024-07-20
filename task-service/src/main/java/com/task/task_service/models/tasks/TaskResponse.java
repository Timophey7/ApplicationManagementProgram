package com.task.task_service.models.tasks;

import com.task.task_service.models.enums.PriorityEnums;
import com.task.task_service.models.enums.TaskCondition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class TaskResponse {

    String taskName;
    PriorityEnums priorityEnums;
    TaskCondition condition;
    String description;
    LocalDateTime startTaskWork;
    LocalDateTime endTaskWork;
    String responsiblePerson;

}
