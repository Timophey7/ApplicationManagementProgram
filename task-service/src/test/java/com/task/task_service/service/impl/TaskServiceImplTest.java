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
    String uniqueCode;

    @BeforeEach
    void setUp() {

        uniqueCode = "werty";

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

        when(taskRepository.findAllTasksByAppUniqueCode(uniqueCode)).thenReturn(List.of(task));

        List<TaskResponse> tasksByApp = taskService.getTasksByApp(uniqueCode);

        assertEquals(List.of(taskResponse),tasksByApp);
        verify(taskRepository).findAllTasksByAppUniqueCode(uniqueCode);

    }

    @Test
    void mapToTaskResponseShouldReturnTaskResponse() {


        TaskResponse mapToTaskResponse = taskService.mapToTaskResponse(task);

        assertEquals(taskResponse,mapToTaskResponse);

    }

    @Test
    void getSortedTasksShouldReturnSortedByLowPriority() {

        PriorityEnums priorityEnums = PriorityEnums.LOW_PRIORITY;

        when(taskRepository.sortTaskByLowPriority(uniqueCode)).thenReturn(List.of(task,task2));

        List<Task> sortedTasks = taskService.getSortedTasks(uniqueCode, priorityEnums);

        assertEquals(task,sortedTasks.get(0));
        assertEquals(task2,sortedTasks.get(1));
        assertNotEquals(0,sortedTasks.size());
        verify(taskRepository).sortTaskByLowPriority(uniqueCode);

    }

    @Test
    void getSortedTasksShouldReturnSortedByHighPriority() {

        PriorityEnums priorityEnums = PriorityEnums.HIGH_PRIORITY;

        when(taskRepository.sortTaskByHighPriority(uniqueCode)).thenReturn(List.of(task2,task));

        List<Task> sortedTasks = taskService.getSortedTasks(uniqueCode, priorityEnums);

        assertEquals(task2,sortedTasks.get(0));
        assertEquals(task,sortedTasks.get(1));
        assertNotEquals(0,sortedTasks.size());
        verify(taskRepository).sortTaskByHighPriority(uniqueCode);

    }

    @Test
    void getSortedTaskByDateShouldReturnSortedByClosestDateList() {

        DateEnums dateEnums = DateEnums.CLOSEST;

        when(taskRepository.sortTaskByClosestDate(uniqueCode)).thenReturn(List.of(task,task2));

        List<Task> sortedTaskByDate = taskService.getSortedTaskByDate(uniqueCode, dateEnums);

        assertEquals(task,sortedTaskByDate.get(0));
        verify(taskRepository).sortTaskByClosestDate(uniqueCode);

    }

    @Test
    void getSortedTaskByDateShouldReturnSortedByDistantDateList() {

        DateEnums dateEnums = DateEnums.DISTANT;

        when(taskRepository.sortTaskByDistantDate(uniqueCode)).thenReturn(List.of(task2,task));

        List<Task> sortedTaskByDate = taskService.getSortedTaskByDate(uniqueCode, dateEnums);

        assertEquals(task2,sortedTaskByDate.get(0));
        verify(taskRepository).sortTaskByDistantDate(uniqueCode);

    }

    @Test
    void saveTaskShouldSaveAndReturnTask() throws AppNotFoundException {
        App app = new App();

        when(appRepository.findAppByUniqueCode(uniqueCode)).thenReturn(Optional.of(app));
        when(taskRepository.save(task)).thenReturn(task);

        Task saveTask = taskService.saveTask(uniqueCode, task);

        assertEquals(task,saveTask);
        verify(taskRepository).save(task);

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