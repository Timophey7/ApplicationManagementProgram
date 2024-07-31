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
    public List<TaskResponse> getTasksByApp(String uniqueCode) {
        return taskRepository.findAllTasksByAppUniqueCode(uniqueCode)
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
    public List<Task> getSortedTasks(String uniqueCode, PriorityEnums enums) {
        if (enums.equals(PriorityEnums.LOW_PRIORITY)){
            return taskRepository.sortTaskByLowPriority(uniqueCode);
        } else if (enums.equals(PriorityEnums.HIGH_PRIORITY)) {
            return taskRepository.sortTaskByHighPriority(uniqueCode);
        }
        return List.of();
    }

    @Override
    public List<Task> getSortedTaskByDate(String uniqueCode, DateEnums enums) {
        if (enums.equals(DateEnums.CLOSEST)){
            return taskRepository.sortTaskByClosestDate(uniqueCode);
        }else if (enums.equals(DateEnums.DISTANT)){
            return taskRepository.sortTaskByDistantDate(uniqueCode);
        }
        return List.of();
    }

    @Override
    public Task saveTask(String uniqueCode, Task task) throws AppNotFoundException {
        App app = appRepository.findAppByUniqueCode(uniqueCode)
                .orElseThrow(() -> new AppNotFoundException("app not found"));

        task.setAppUniqueCode(uniqueCode);
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
