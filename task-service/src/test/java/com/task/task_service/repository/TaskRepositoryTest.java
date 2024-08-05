package com.task.task_service.repository;

import com.task.task_service.models.app.App;
import com.task.task_service.models.enums.PriorityEnums;
import com.task.task_service.models.enums.TaskCondition;
import com.task.task_service.models.tasks.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private AppRepository appRepository;

    private App app;
    private Task task1;
    private Task task2;

    @BeforeEach
    void setUp() {

        task1 = new Task();
        task1.setTaskName("Task High Priority");
        task1.setPriorityEnums(PriorityEnums.HIGH_PRIORITY);
        task1.setAppUniqueCode("uniqueCode");
        task1.setStartTaskWork(LocalDateTime.of(2024, 8, 1, 12, 45, 12));
        task1.setEndTaskWork(LocalDateTime.of(2024, 8, 10, 12, 45, 12));
        task1.setCondition(TaskCondition.IN_TESTING);
        task1.setResponsiblePerson("Alex");

        task2 = new Task();
        task2.setTaskName("Task Low Priority");
        task2.setPriorityEnums(PriorityEnums.LOW_PRIORITY);
        task2.setAppUniqueCode("uniqueCode");
        task2.setStartTaskWork(LocalDateTime.of(2024, 8, 1, 12, 45, 12));
        task2.setEndTaskWork(LocalDateTime.of(2024, 9, 1, 12, 45, 12));
        task2.setCondition(TaskCondition.IN_TESTING);
        task2.setResponsiblePerson("Sam");

        taskRepository.save(task1);
        taskRepository.save(task2);
    }

    @AfterEach
    void tearDown() {
        taskRepository.deleteAll();
    }

    @Test
    void sortTaskByHighPriority() {
        List<Task> sortedTasks = taskRepository.sortTaskByHighPriority("uniqueCode");
        assertEquals(2, sortedTasks.size());
        assertEquals("Task High Priority", sortedTasks.get(0).getTaskName());
    }

    @Test
    void sortTaskByLowPriority() {
        List<Task> sortedTasks = taskRepository.sortTaskByLowPriority("uniqueCode");
        assertEquals(2, sortedTasks.size());
        assertEquals("Task Low Priority", sortedTasks.get(0).getTaskName());
    }

    @Test
    void sortTaskByClosestDate() {
        List<Task> sortedTasks = taskRepository.sortTaskByClosestDate("uniqueCode");
        assertEquals(task1,sortedTasks.get(0));
        assertEquals(2, sortedTasks.size());
    }

    @Test
    void sortTaskByDistantDate() {
        List<Task> sortedTasks = taskRepository.sortTaskByDistantDate("uniqueCode");
        assertEquals(task2,sortedTasks.get(0));
        assertEquals(2, sortedTasks.size());
    }

    @Test
    void findAllTasksByAppUniqueCode() {
        List<Task> tasks = taskRepository.findAllTasksByAppUniqueCode("uniqueCode");
        assertEquals(2, tasks.size());
    }
}