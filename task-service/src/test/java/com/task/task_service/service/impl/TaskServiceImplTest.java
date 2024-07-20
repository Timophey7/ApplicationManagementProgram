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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    TaskRepository taskRepository;
    @Mock
    AppRepository appRepository;
    @InjectMocks
    TaskServiceImpl taskService;

    Task task;
    Task task2;
    TaskResponse taskResponse;

    @BeforeEach
    void setUp() {

        task2 = new Task();
        task2.setId(1);
        task2.setTaskName("fix backend");
        task2.setDescription("fix backend and create some test");
        task2.setResponsiblePerson("Alex");
        task2.setPriorityEnums(PriorityEnums.HIGH_PRIORITY);
        task2.setStartTaskWork(LocalDateTime.now());
        task2.setEndTaskWork(LocalDateTime.of(2024,7,24,8,59,54));

        task = new Task();
        task.setId(1);
        task.setTaskName("fix backend");
        task.setDescription("fix backend and create some test");
        task.setResponsiblePerson("Alex");
        task.setPriorityEnums(PriorityEnums.LOW_PRIORITY);
        task.setStartTaskWork(LocalDateTime.now());
        task.setEndTaskWork(LocalDateTime.of(2024,7,19,8,59,54));

        taskResponse = new TaskResponse();
        taskResponse.setTaskName("fix backend");
        taskResponse.setDescription("fix backend and create some test");
        taskResponse.setResponsiblePerson("Alex");
        taskResponse.setPriorityEnums(PriorityEnums.LOW_PRIORITY);
        taskResponse.setStartTaskWork(LocalDateTime.now());
        taskResponse.setEndTaskWork(LocalDateTime.of(2024,7,19,8,59,54));
    }

    @Test
    void getTasksByAppShouldReturnTaskResponse() {
        int appId = 1;
        when(taskRepository.findAllTasksByAppId(appId)).thenReturn(List.of(task));

        List<TaskResponse> tasksByApp = taskService.getTasksByApp(appId);

        assertEquals(List.of(taskResponse),tasksByApp);
        verify(taskRepository).findAllTasksByAppId(appId);

    }

    @Test
    void mapToTaskResponseShouldReturnTaskResponse() {


        TaskResponse mapToTaskResponse = taskService.mapToTaskResponse(task);

        assertEquals(taskResponse,mapToTaskResponse);

    }

    @Test
    void getSortedTasksShouldReturnSortedByLowPriority() {

        int appId = 1;
        PriorityEnums priorityEnums = PriorityEnums.LOW_PRIORITY;

        when(taskRepository.sortTaskByLowPriority(appId)).thenReturn(List.of(task,task2));

        List<Task> sortedTasks = taskService.getSortedTasks(appId, priorityEnums);

        assertEquals(task,sortedTasks.get(0));
        assertEquals(task2,sortedTasks.get(1));
        assertNotEquals(0,sortedTasks.size());
        verify(taskRepository).sortTaskByLowPriority(appId);

    }

    @Test
    void getSortedTasksShouldReturnSortedByHighPriority() {

        int appId = 1;
        PriorityEnums priorityEnums = PriorityEnums.HIGH_PRIORITY;

        when(taskRepository.sortTaskByHighPriority(appId)).thenReturn(List.of(task2,task));

        List<Task> sortedTasks = taskService.getSortedTasks(appId, priorityEnums);

        assertEquals(task2,sortedTasks.get(0));
        assertEquals(task,sortedTasks.get(1));
        assertNotEquals(0,sortedTasks.size());
        verify(taskRepository).sortTaskByHighPriority(appId);

    }

    @Test
    void getSortedTaskByDateShouldReturnSortedByClosestDateList() {

        int appId = 1;
        DateEnums dateEnums = DateEnums.CLOSEST;

        when(taskRepository.sortTaskByClosestDate(appId)).thenReturn(List.of(task,task2));

        List<Task> sortedTaskByDate = taskService.getSortedTaskByDate(appId, dateEnums);

        assertEquals(task,sortedTaskByDate.get(0));
        verify(taskRepository).sortTaskByClosestDate(appId);

    }

    @Test
    void getSortedTaskByDateShouldReturnSortedByDistantDateList() {

        int appId = 1;
        DateEnums dateEnums = DateEnums.DISTANT;

        when(taskRepository.sortTaskByDistantDate(appId)).thenReturn(List.of(task2,task));

        List<Task> sortedTaskByDate = taskService.getSortedTaskByDate(appId, dateEnums);

        assertEquals(task2,sortedTaskByDate.get(0));
        verify(taskRepository).sortTaskByDistantDate(appId);

    }

    @Test
    void saveTaskShouldSaveAndReturnTask() throws AppNotFoundException {
        App app = new App();
        int appId = 1;

        when(appRepository.findById(appId)).thenReturn(Optional.of(app));
        when(taskRepository.save(task)).thenReturn(task);

        Task saveTask = taskService.saveTask(appId, task);

        assertEquals(task,saveTask);
        assertEquals(app,saveTask.getApp());
        verify(taskRepository).save(task);

    }

    @Test
    void saveTaskShouldThrowException() throws AppNotFoundException {
        App app = new App();
        int appId = 1;

        when(appRepository.findById(appId).orElseThrow()).thenThrow(AppNotFoundException.class);

        Task saveTask = taskService.saveTask(appId, task);


    }

    @Test
    void setCondition() throws TaskNotFoundException {
        int taskId = 1;
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);

        taskService.setCondition(taskId,"TO_BE_EXECUTED");

        assertEquals(task.getCondition(), TaskCondition.TO_BE_EXECUTED);
        verify(taskRepository).findById(taskId);
        verify(taskRepository).save(task);
    }

    @Test
    void getTaskResponseById() throws TaskNotFoundException {

        int taskId = 1;

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        TaskResponse taskResponseById = taskService.getTaskResponseById(taskId);

        assertNotNull(taskResponseById);
    }
}