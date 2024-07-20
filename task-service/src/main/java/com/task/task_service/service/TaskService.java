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

    List<TaskResponse> getTasksByApp(int appId);

    List<Task> getSortedTasks(int appId, PriorityEnums enums);

    List<Task> getSortedTaskByDate(int appId, DateEnums enums);

    Task saveTask(int appId,Task task) throws AppNotFoundException;

    void setCondition(int taskId,String condition) throws TaskNotFoundException;

    TaskResponse getTaskResponseById(int taskId) throws TaskNotFoundException;
}
