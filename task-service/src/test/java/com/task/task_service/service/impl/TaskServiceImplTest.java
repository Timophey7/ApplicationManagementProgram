package com.task.task_service.service.impl;

import com.task.task_service.exceptions.AppNotFoundException;
import com.task.task_service.exceptions.TaskAlreadyExistsException;
import com.task.task_service.exceptions.TaskNotFoundException;
import com.task.task_service.models.app.App;
import com.task.task_service.models.enums.PriorityEnums;
import com.task.task_service.models.enums.TaskCondition;
import com.task.task_service.models.tasks.Task;
import com.task.task_service.models.tasks.TaskDTO;
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
    int pageNum;
    int value;

    @BeforeEach
    public void setUp() {
        pageNum = 0;
        value = 10;
    }

    @Test
    void testGetTasksByApp() throws TaskNotFoundException {
        Task task1 = new Task();
        task1.setTaskName("Task 1");
        task1.setDescription("Description 1");
        task1.setPriorityEnums(PriorityEnums.HIGH_PRIORITY);

        Task task2 = new Task();
        task2.setTaskName("Task 2");
        task2.setDescription("Description 2");
        task2.setPriorityEnums(PriorityEnums.LOW_PRIORITY);

        when(taskRepository.getAllAppTasks(pageNum,value,UNIQUE_CODE)).thenReturn(Optional.of(Arrays.asList(task1, task2)));

        List<TaskResponse> result = taskService.getTasksByApp(UNIQUE_CODE, pageNum, value);

        assertEquals(2, result.size());
        assertEquals("Task 1", result.get(0).getTaskName());
        assertEquals("Task 2", result.get(1).getTaskName());
    }

    @Test
    void testGetTasks_SortedByPriority_LowPriority() throws TaskNotFoundException {
        Task task1 = new Task();
        task1.setTaskName("Low Priority Task 1");
        task1.setPriorityEnums(PriorityEnums.LOW_PRIORITY);

        when(taskRepository.sortTaskByLowPriority(UNIQUE_CODE,pageNum,value)).thenReturn(Optional.of(Arrays.asList(task1)));

        List<TaskResponse> result = taskService.getTasksSortedByPriority(UNIQUE_CODE, PriorityEnums.LOW_PRIORITY, pageNum, value);

        assertEquals(1, result.size());
        assertEquals("Low Priority Task 1", result.get(0).getTaskName());
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
        Task task = new Task();
        task.setId(1);
        task.setTaskName("Task by ID");

        when(taskRepository.findById(1)).thenReturn(Optional.of(task));

        TaskResponse result = taskService.getTaskResponseById(1);

        assertEquals("Task by ID", result.getTaskName());
    }
}