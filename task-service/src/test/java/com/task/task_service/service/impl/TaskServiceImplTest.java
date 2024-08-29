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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @InjectMocks
    private TaskServiceImpl taskService;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private AppRepository appRepository;

    @Mock
    ModelMapper modelMapper;

    private static final String UNIQUE_CODE = "uniqueCode";
    private static final int PAGE_NUM = 0;
    private static final int VALUE = 10;


    @Test
    void testGetTasksByApp_Success() throws TaskNotFoundException {
        List<Task> tasks = List.of(new Task());
        when(taskRepository.getAllAppTasks(PAGE_NUM * VALUE, VALUE, UNIQUE_CODE))
                .thenReturn(Optional.of(tasks));

        List<TaskResponse> result = taskService.getTasksByApp(UNIQUE_CODE, PAGE_NUM, VALUE);

        assertEquals(1, result.size());
    }

    @Test
    void testGetTasksByApp_NoTasksFound() {
        when(taskRepository.getAllAppTasks(PAGE_NUM * VALUE, VALUE, UNIQUE_CODE))
                .thenReturn(Optional.empty());

        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class, () -> {
            taskService.getTasksByApp(UNIQUE_CODE, PAGE_NUM, VALUE);
        });

        assertEquals("Tasks not found", exception.getMessage());
    }

    @Test
    void testGetTasksSortedByPriority_Success() throws TaskNotFoundException {
        Task task = new Task();
        task.setTaskName("high");
        task.setPriorityEnums(PriorityEnums.HIGH_PRIORITY);
        Task task1 = new Task();
        task1.setTaskName("low");
        task1.setPriorityEnums(PriorityEnums.LOW_PRIORITY);
        List<Task> tasks = List.of(task1,task);
        when(taskRepository.sortTaskByLowPriority(UNIQUE_CODE, PAGE_NUM, VALUE))
                .thenReturn(Optional.of(tasks));

        List<TaskResponse> result = taskService.getTasksSortedByPriority(UNIQUE_CODE, PriorityEnums.LOW_PRIORITY, PAGE_NUM, VALUE);

        assertEquals(2, result.size());
    }

    @Test
    void testGetTasksSortedByPriority_NoTasksFound() {
        when(taskRepository.sortTaskByLowPriority(UNIQUE_CODE, PAGE_NUM, VALUE))
                .thenReturn(Optional.empty());

        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class, () -> {
            taskService.getTasksSortedByPriority(UNIQUE_CODE, PriorityEnums.LOW_PRIORITY, PAGE_NUM, VALUE);
        });

        assertEquals("Tasks not found", exception.getMessage());
    }

    @Test
    void testGetTasksSortedByDate_Success() throws TaskNotFoundException {
        List<Task> tasks = List.of(new Task());
        when(taskRepository.sortTaskByClosestDate(UNIQUE_CODE, PAGE_NUM, VALUE))
                .thenReturn(Optional.of(tasks));

        List<TaskResponse> result = taskService.getTasksSortedByDate(UNIQUE_CODE, DateEnums.CLOSEST, PAGE_NUM, VALUE);

        assertEquals(1, result.size());
    }

    @Test
    void testGetTasksSortedByDate_NoTasksFound() {
        when(taskRepository.sortTaskByClosestDate(UNIQUE_CODE, PAGE_NUM, VALUE))
                .thenReturn(Optional.empty());

        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class, () -> {
            taskService.getTasksSortedByDate(UNIQUE_CODE, DateEnums.CLOSEST, PAGE_NUM, VALUE);
        });

        assertEquals("Tasks not found", exception.getMessage());
    }

    @Test
    void testSaveTask_AppNotFound() {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setTaskName("New Task");

        when(appRepository.findAppByUniqueCode(UNIQUE_CODE)).thenReturn(Optional.empty());

        assertThrows(AppNotFoundException.class, () -> taskService.saveTask(UNIQUE_CODE, taskDTO));
    }

    @Test
    void testSaveTask_Success() throws AppNotFoundException, TaskAlreadyExistsException {
        App app = new App();
        app.setUniqueCode(UNIQUE_CODE);

        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setTaskName("New Task");

        when(appRepository.findAppByUniqueCode(UNIQUE_CODE)).thenReturn(Optional.of(app));

        Task result = taskService.saveTask(UNIQUE_CODE, taskDTO);

        assertEquals("New Task", result.getTaskName());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void testSetCondition_TaskNotFound() {
        when(taskRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.setCondition(1, "COMPLETE"));
    }

    @Test
    void testSetCondition_Success() throws TaskNotFoundException {
        Task task = new Task();
        task.setId(1);

        when(taskRepository.findById(1)).thenReturn(Optional.of(task));

        taskService.setCondition(1, "COMPLETE");

        assertEquals(TaskCondition.COMPLETE, task.getCondition());
        verify(taskRepository).save(task);
    }

    @Test
    void testGetTaskResponseById_TaskNotFound() {
        when(taskRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.getTaskResponseById(1));
    }

    @Test
    void testGetTaskResponseById_Success() throws TaskNotFoundException {
        TaskResponse taskResponse = new TaskResponse();
        taskResponse.setTaskName("Task by ID");
        Task task = new Task();
        task.setId(1);
        task.setTaskName("Task by ID");

        when(modelMapper.map(task,TaskResponse.class)).thenReturn(taskResponse);
        when(taskRepository.findById(1)).thenReturn(Optional.of(task));

        TaskResponse result = taskService.getTaskResponseById(1);

        assertEquals("Task by ID", result.getTaskName());
    }
}