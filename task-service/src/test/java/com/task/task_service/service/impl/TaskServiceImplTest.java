package com.task.task_service.service.impl;

import com.task.task_service.exceptions.AppNotFoundException;
import com.task.task_service.exceptions.TaskNotFoundException;
import com.task.task_service.models.app.App;
import com.task.task_service.models.enums.PriorityEnums;
import com.task.task_service.models.enums.TaskCondition;
import com.task.task_service.models.tasks.Task;
import com.task.task_service.models.tasks.TaskResponse;
import com.task.task_service.repository.AppRepository;
import com.task.task_service.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
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

    private static final String UNIQUE_CODE = "uniqueCode";

    @BeforeEach
    public void setUp() {

    }

    @Test
    public void testGetTasksByApp() {
        Task task1 = new Task();
        task1.setTaskName("Task 1");
        task1.setDescription("Description 1");
        task1.setPriorityEnums(PriorityEnums.HIGH_PRIORITY);

        Task task2 = new Task();
        task2.setTaskName("Task 2");
        task2.setDescription("Description 2");
        task2.setPriorityEnums(PriorityEnums.LOW_PRIORITY);

        when(taskRepository.findAllTasksByAppUniqueCode(UNIQUE_CODE)).thenReturn(Arrays.asList(task1, task2));

        List<TaskResponse> result = taskService.getTasksByApp(UNIQUE_CODE);

        assertEquals(2, result.size());
        assertEquals("Task 1", result.get(0).getTaskName());
        assertEquals("Task 2", result.get(1).getTaskName());
    }

    @Test
    public void testGetSortedTasks_LowPriority() {
        Task task1 = new Task();
        task1.setTaskName("Low Priority Task 1");
        task1.setPriorityEnums(PriorityEnums.LOW_PRIORITY);

        when(taskRepository.sortTaskByLowPriority(UNIQUE_CODE)).thenReturn(Arrays.asList(task1));

        List<TaskResponse> result = taskService.getSortedTasks(UNIQUE_CODE, PriorityEnums.LOW_PRIORITY);

        assertEquals(1, result.size());
        assertEquals("Low Priority Task 1", result.get(0).getTaskName());
    }

    @Test
    public void testSaveTask_AppNotFound() {
        Task task = new Task();
        task.setTaskName("New Task");

        when(appRepository.findAppByUniqueCode(UNIQUE_CODE)).thenReturn(Optional.empty());

        assertThrows(AppNotFoundException.class, () -> taskService.saveTask(UNIQUE_CODE, task));
    }

    @Test
    public void testSaveTask_Success() throws AppNotFoundException {
        App app = new App();
        app.setUniqueCode(UNIQUE_CODE);

        Task task = new Task();
        task.setTaskName("New Task");

        when(appRepository.findAppByUniqueCode(UNIQUE_CODE)).thenReturn(Optional.of(app));

        Task result = taskService.saveTask(UNIQUE_CODE, task);

        assertEquals("New Task", result.getTaskName());
        verify(taskRepository).save(task);
    }

    @Test
    public void testSetCondition_TaskNotFound() {
        when(taskRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.setCondition(1, "COMPLETE"));
    }

    @Test
    public void testSetCondition_Success() throws TaskNotFoundException {
        Task task = new Task();
        task.setId(1);

        when(taskRepository.findById(1)).thenReturn(Optional.of(task));

        taskService.setCondition(1, "COMPLETE");

        assertEquals(TaskCondition.COMPLETE, task.getCondition());
        verify(taskRepository).save(task);
    }

    @Test
    public void testGetTaskResponseById_TaskNotFound() {
        when(taskRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.getTaskResponseById(1));
    }

    @Test
    public void testGetTaskResponseById_Success() throws TaskNotFoundException {
        Task task = new Task();
        task.setId(1);
        task.setTaskName("Task by ID");

        when(taskRepository.findById(1)).thenReturn(Optional.of(task));

        TaskResponse result = taskService.getTaskResponseById(1);

        assertEquals("Task by ID", result.getTaskName());
    }
}