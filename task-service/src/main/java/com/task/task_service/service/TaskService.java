package com.task.task_service.service;

import com.task.task_service.exceptions.AppNotFoundException;
import com.task.task_service.exceptions.TaskAlreadyExistsException;
import com.task.task_service.exceptions.TaskNotFoundException;
import com.task.task_service.models.enums.DateEnums;
import com.task.task_service.models.enums.PriorityEnums;
import com.task.task_service.models.tasks.Task;
import com.task.task_service.models.tasks.TaskDTO;
import com.task.task_service.models.tasks.TaskResponse;

import java.util.List;


public interface TaskService {

    TaskResponse mapToTaskResponse(Task task);

    List<TaskResponse> getTasksByApp(String uniqueCode, int pageNum, int value) throws TaskNotFoundException;

    List<TaskResponse> getSortedTasks(String uniqueCode, PriorityEnums enums, int pageNum, int value) throws TaskNotFoundException;

    List<TaskResponse> getSortedTaskByDate(String uniqueCode, DateEnums enums, int pageNum, int value) throws TaskNotFoundException;

    Task saveTask(String uniqueCode, TaskDTO task) throws AppNotFoundException, TaskAlreadyExistsException;

    void setCondition(int taskId,String condition) throws TaskNotFoundException;

    TaskResponse getTaskResponseById(int taskId) throws TaskNotFoundException;
}
