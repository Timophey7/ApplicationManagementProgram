package com.task.task_service.service.impl;

import com.task.task_service.exceptions.AppNotFoundException;
import com.task.task_service.exceptions.TaskAlreadyExistsException;
import com.task.task_service.exceptions.TaskNotFoundException;
import com.task.task_service.models.app.App;
import com.task.task_service.models.enums.DateEnums;
import com.task.task_service.models.enums.PriorityEnums;
import com.task.task_service.models.enums.TaskCondition;
import com.task.task_service.models.tasks.Task;
import com.task.task_service.models.tasks.TaskDTO;
import com.task.task_service.models.tasks.TaskResponse;
import com.task.task_service.repository.AppRepository;
import com.task.task_service.repository.TaskRepository;
import com.task.task_service.service.TaskService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
public class TaskServiceImpl implements TaskService {

    TaskRepository taskRepository;
    AppRepository appRepository;

    private static final String TASK_EXCEPTION_MESSAGE = "tasks not found";

    @Override
    @Cacheable(value = "allTasks",key = "#uniqueCode")
    public List<TaskResponse> getTasksByApp(String uniqueCode, int pageNum, int value) throws TaskNotFoundException {
         return taskRepository.getAllAppTasks(pageNum*value,value,uniqueCode)
                .orElseThrow(() -> new TaskNotFoundException("not found"))
                 .stream()
                 .map(this::mapToTaskResponse)
                 .toList();
    }

    @Override
    @Cacheable(value = "sortedByPriorityTasks",key = "#uniqueCode")
    public List<TaskResponse> getSortedTasks(String uniqueCode, PriorityEnums enums, int pageNum, int value) throws TaskNotFoundException {
        if (enums.equals(PriorityEnums.LOW_PRIORITY)){
            return taskRepository.sortTaskByLowPriority(uniqueCode,pageNum*value,value)
                    .orElseThrow(() -> new TaskNotFoundException(TASK_EXCEPTION_MESSAGE))
                    .stream().map(this::mapToTaskResponse)
                    .toList();
        } else if (enums.equals(PriorityEnums.HIGH_PRIORITY)) {
            return taskRepository.sortTaskByHighPriority(uniqueCode,pageNum*value,value)
                    .orElseThrow(() -> new TaskNotFoundException(TASK_EXCEPTION_MESSAGE))
                    .stream().map(this::mapToTaskResponse)
                    .toList();
        }
        return List.of();
    }

    @Override
    @Cacheable(value = "sortedByDateTasks",key = "#uniqueCode")
    public List<TaskResponse> getSortedTaskByDate(String uniqueCode, DateEnums enums, int pageNum, int value) throws TaskNotFoundException {
        if (enums.equals(DateEnums.CLOSEST)){
            return taskRepository.sortTaskByClosestDate(uniqueCode,pageNum*value,value)
                    .orElseThrow(() -> new TaskNotFoundException(TASK_EXCEPTION_MESSAGE))
                    .stream().map(this::mapToTaskResponse)
                    .toList();
        }else if (enums.equals(DateEnums.DISTANT)){
            return taskRepository.sortTaskByDistantDate(uniqueCode,pageNum*value,value)
                    .orElseThrow(() -> new TaskNotFoundException(TASK_EXCEPTION_MESSAGE))
                    .stream().map(this::mapToTaskResponse)
                    .toList();
        }
        return List.of();
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "allTasks",allEntries = true),
            @CacheEvict(value = "sortedByPriorityTasks",allEntries = true),
            @CacheEvict(value = "sortedByDateTasks",allEntries = true)
    })
    public synchronized Task saveTask(String uniqueCode, TaskDTO taskDTO) throws AppNotFoundException, TaskAlreadyExistsException {
        if (taskRepository.existsByTaskNameAndAppUniqueCode(taskDTO.getTaskName(),uniqueCode)){
            throw new TaskAlreadyExistsException("task already exists");
        }
        Task task = mapToTask(uniqueCode, taskDTO);
        taskRepository.save(task);
        return task;
    }

    private Task mapToTask(String uniqueCode,TaskDTO taskDTO) throws AppNotFoundException {
        App app = appRepository.findAppByUniqueCode(uniqueCode)
                .orElseThrow(() -> new AppNotFoundException("app not found"));
        return Task.builder()
                .app(app)
                .appUniqueCode(uniqueCode)
                .taskName(taskDTO.getTaskName())
                .condition(taskDTO.getCondition())
                .startTaskWork(taskDTO.getStartTaskWork())
                .description(taskDTO.getDescription())
                .responsiblePerson(taskDTO.getResponsiblePerson())
                .priorityEnums(taskDTO.getPriorityEnums())
                .endTaskWork(taskDTO.getEndTaskWork())
                .build();
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


}
