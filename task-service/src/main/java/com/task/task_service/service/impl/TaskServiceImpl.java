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
import com.task.task_service.service.TriFunction;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TaskServiceImpl implements TaskService {

    TaskRepository taskRepository;
    AppRepository appRepository;
    ModelMapper modelMapper;
    private static final String TASK_EXCEPTION_MESSAGE = "Tasks not found";

    @Override
    @Cacheable(value = "allTasks", key = "#uniqueCode")
    public List<TaskResponse> getTasksByApp(final String uniqueCode, final int pageNum, final int value) throws TaskNotFoundException {
        return handleTasksResponse(taskRepository.getAllAppTasks(pageNum * value, value, uniqueCode));
    }

    @Override
    @Cacheable(value = "sortedByPriorityTasks", key = "#uniqueCode")
    public List<TaskResponse> getTasksSortedByPriority(final String uniqueCode, final PriorityEnums priority, final int pageNum, final int value) throws TaskNotFoundException {
        return switch (priority) {
            case LOW_PRIORITY -> getSortedTasks(uniqueCode, pageNum, value, taskRepository::sortTaskByLowPriority);
            case HIGH_PRIORITY -> getSortedTasks(uniqueCode, pageNum, value, taskRepository::sortTaskByHighPriority);
            default -> List.of();
        };
    }

    @Override
    @Cacheable(value = "sortedByDateTasks", key = "#uniqueCode")
    public List<TaskResponse> getTasksSortedByDate(final String uniqueCode, final DateEnums enums, final int pageNum, final int value) throws TaskNotFoundException {
        return switch (enums) {
            case CLOSEST -> getSortedTasks(uniqueCode, pageNum, value, taskRepository::sortTaskByClosestDate);
            case DISTANT -> getSortedTasks(uniqueCode, pageNum, value, taskRepository::sortTaskByDistantDate);
            default -> List.of();
        };
    }

    private List<TaskResponse> getSortedTasks(final String uniqueCode, final int pageNum, final int value,
                                              TriFunction<String, Integer, Integer, Optional<List<Task>>> sortingFunction) throws TaskNotFoundException {
        return handleTasksResponse(sortingFunction.apply(uniqueCode, pageNum, value));
    }

    private List<TaskResponse> handleTasksResponse(Optional<List<Task>> tasks) throws TaskNotFoundException {
        return tasks.orElseThrow(() -> new TaskNotFoundException(TASK_EXCEPTION_MESSAGE))
                .stream()
                .map(this::mapToTaskResponse)
                .toList();
    }


    @Override
    @Caching(evict = {
            @CacheEvict(value = "allTasks", allEntries = true),
            @CacheEvict(value = "sortedByPriorityTasks", allEntries = true),
            @CacheEvict(value = "sortedByDateTasks", allEntries = true)
    })
    public synchronized Task saveTask(
            final String uniqueCode,
            final TaskDTO taskDTO
    ) throws AppNotFoundException, TaskAlreadyExistsException {
        if (taskRepository.existsByTaskNameAndAppUniqueCode(taskDTO.getTaskName(), uniqueCode)) {
            throw new TaskAlreadyExistsException("task already exists");
        }
        Task task = mapToTask(uniqueCode, taskDTO);
        taskRepository.save(task);
        return task;
    }

    private Task mapToTask(final String uniqueCode, final TaskDTO taskDTO) throws AppNotFoundException {
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
    public void setCondition(final int taskId, final String condition) throws TaskNotFoundException {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("task not found"));
        TaskCondition taskCondition = TaskCondition.valueOf(condition);
        task.setCondition(taskCondition);
        taskRepository.save(task);
    }

    @Override
    public TaskResponse getTaskResponseById(final int taskId) throws TaskNotFoundException {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("task not found"));

        return mapToTaskResponse(task);
    }

    @Override
    public TaskResponse mapToTaskResponse(final Task task) {
        return modelMapper.map(task,TaskResponse.class);
    }


}
