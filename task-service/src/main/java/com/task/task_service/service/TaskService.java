package com.task.task_service.service;

import com.task.task_service.exceptions.AppNotFoundException;
import com.task.task_service.exceptions.TaskNotFoundException;
import com.task.task_service.models.enums.DateEnums;
import com.task.task_service.models.enums.PriorityEnums;
import com.task.task_service.models.tasks.Task;
import com.task.task_service.models.tasks.TaskResponse;

import java.util.List;


public interface TaskService {

    TaskResponse mapToTaskResponse(Task task);

    List<TaskResponse> getTasksByApp(String uniqueCode);

    List<TaskResponse> getSortedTasks(String uniqueCode, PriorityEnums enums);

    List<TaskResponse> getSortedTaskByDate(String uniqueCode, DateEnums enums);

    Task saveTask(String uniqueCode,Task task) throws AppNotFoundException;

    void setCondition(int taskId,String condition) throws TaskNotFoundException;

    TaskResponse getTaskResponseById(int taskId) throws TaskNotFoundException;
}
