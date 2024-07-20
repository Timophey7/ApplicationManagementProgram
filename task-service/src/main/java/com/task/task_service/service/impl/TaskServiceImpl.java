package com.task.task_service.service.impl;

import com.task.task_service.exceptions.AppNotFoundException;
import com.task.task_service.exceptions.TaskNotFoundException;
import com.task.task_service.models.app.App;
import com.task.task_service.models.enums.DateEnums;
import com.task.task_service.models.enums.PriorityEnums;
import com.task.task_service.models.enums.TaskCondition;
import com.task.task_service.models.tasks.Task;
import com.task.task_service.models.tasks.TaskResponse;
import com.task.task_service.repository.AppRepository;
import com.task.task_service.repository.TaskRepository;
import com.task.task_service.service.TaskService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
public class TaskServiceImpl implements TaskService {

    TaskRepository taskRepository;
    AppRepository appRepository;


    @Override
    public List<TaskResponse> getTasksByApp(int appId) {
        return taskRepository.findAllTasksByAppId(appId)
                .stream()
                .map(el -> mapToTaskResponse(el))
                .collect(Collectors.toList());
    }

    @Override
    public TaskResponse mapToTaskResponse(Task task) {
        return TaskResponse.builder()
                .taskName(task.getTaskName())
                .description(task.getDescription())
                .priorityEnums(task.getPriorityEnums())
                .condition(task.getCondition())
                .startTaskWork(task.getStartTaskWork())
                .endTaskWork(task.getEndTaskWork())
                .responsiblePerson(task.getResponsiblePerson())
                .build();
    }

    @Override
    public List<Task> getSortedTasks(int appId, PriorityEnums enums) {
        if (enums.equals(PriorityEnums.LOW_PRIORITY)){
            return taskRepository.sortTaskByLowPriority(appId);
        } else if (enums.equals(PriorityEnums.HIGH_PRIORITY)) {
            return taskRepository.sortTaskByHighPriority(appId);
        }
        return List.of();
    }

    @Override
    public List<Task> getSortedTaskByDate(int appId, DateEnums enums) {
        if (enums.equals(DateEnums.CLOSEST)){
            return taskRepository.sortTaskByClosestDate(appId);
        }else if (enums.equals(DateEnums.DISTANT)){
            return taskRepository.sortTaskByDistantDate(appId);
        }
        return List.of();
    }

    @Override
    public Task saveTask(int appId, Task task) throws AppNotFoundException {
        App app = appRepository.findById(appId)
                .orElseThrow(() -> new AppNotFoundException("app not found"));

        task.setApp(app);
        taskRepository.save(task);
        return task;
    }

    @Override
    public void setCondition(int taskId,String condition) throws TaskNotFoundException {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("task not found"));
        TaskCondition taskCondition = TaskCondition.valueOf(condition);
        task.setCondition(taskCondition);
        taskRepository.save(task);
    }

    @Override
    public TaskResponse getTaskResponseById(int taskId) throws TaskNotFoundException {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("task not found"));

        return mapToTaskResponse(task);
    }


}
