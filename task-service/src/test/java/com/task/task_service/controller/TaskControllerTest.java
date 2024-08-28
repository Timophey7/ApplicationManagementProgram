package com.task.task_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.task_service.exceptions.AppNotFoundException;
import com.task.task_service.exceptions.TaskNotFoundException;
import com.task.task_service.models.enums.DateEnums;
import com.task.task_service.models.enums.PriorityEnums;
import com.task.task_service.models.enums.TaskCondition;
import com.task.task_service.models.tasks.Task;
import com.task.task_service.models.tasks.TaskDTO;
import com.task.task_service.models.tasks.TaskResponse;
import com.task.task_service.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.mockito.Mockito.*;

@WebMvcTest(controllers = TaskController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper;

    @MockBean
    TaskService taskService;

    TaskResponse taskResponse1;

    TaskResponse taskResponse2;

    int pageNum;
    int value;

    @BeforeEach
    void setUp() {

        pageNum = 0;
        value = 10;

        objectMapper = new ObjectMapper();
        taskResponse1 = new TaskResponse();
        taskResponse1.setTaskName("TaskName");
        taskResponse1.setCondition(TaskCondition.IN_TESTING);
        taskResponse1.setResponsiblePerson("TestPerson");
        taskResponse1.setPriorityEnums(PriorityEnums.LOW_PRIORITY);
        taskResponse1.setStartTaskWork(LocalDateTime.of(2024,8,12,23,34,12));
        taskResponse1.setEndTaskWork(LocalDateTime.of(2024,8,14,23,34,12));

        taskResponse2 = new TaskResponse();
        taskResponse2.setTaskName("Test");
        taskResponse2.setResponsiblePerson("TestPerson");
        taskResponse2.setPriorityEnums(PriorityEnums.HIGH_PRIORITY);
        taskResponse2.setStartTaskWork(LocalDateTime.of(2024,8,12,23,34,12));
        taskResponse2.setEndTaskWork(LocalDateTime.of(2024,9,12,23,34,12));


    }

    @Test
    void setTaskCondition_Success() throws Exception {
        int taskId = 1;
        String condition = "IN_DEVELOPMENT";

        Task task = new Task();
        task.setId(taskId);
        task.setTaskName("name");

        doNothing().when(taskService).setCondition(taskId,condition);

        ResultActions perform = mockMvc.perform(post("/v1/tracker/apps/{uniqueCode}/tasks/{taskId}/setCondition", "eu123", taskId)
                .param("uniqueCode","eu123")
                .param("taskId", String.valueOf(taskId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(condition)
        );

        perform.andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().string("success"));

    }

    @Test
    void setTaskCondition_TaskNotFound() throws Exception {
        int taskId = 1;
        String condition = "IN_DEVELOPMENT";

        Task task = new Task();
        task.setId(taskId);
        task.setTaskName("name");


        doThrow(new TaskNotFoundException("task not found")).when(taskService).setCondition(taskId,condition);

        ResultActions perform = mockMvc.perform(post("/apps/{uniqueCode}/tasks/{taskId}/setCondition", "uniqueCode", taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(condition)
        );

        perform.andExpect(status().isNotFound());

    }



    @Test
    void getTaskById_Success() throws Exception {
        int taskId = 1;
        when(taskService.getTaskResponseById(taskId)).thenReturn(taskResponse1);

        ResultActions perform = mockMvc.perform(get("/v1/tracker/apps/{uniqueCode}/tasks/{taskId}", "uniqueCode", taskId));

        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.taskName").value("TaskName"))
                .andExpect(jsonPath("$.priorityEnums").value("LOW_PRIORITY"))
                .andDo(print());
    }

    @Test
    void getTaskById_TaskNotFound() throws Exception {
        int taskId = 1;

        when(taskService.getTaskResponseById(taskId)).thenThrow(new TaskNotFoundException("task not found"));

        ResultActions perform = mockMvc.perform(get("/v1/tracker/apps/{uniqueCode}/tasks/{taskId}", "uniqueCode", taskId));

        perform.andExpect(status().isNotFound());
    }

    @Test
    void getAllTasksByApp_Success() throws Exception {

        String uniqueCode = "eu123";

        when(taskService.getTasksByApp(uniqueCode, pageNum, value)).thenReturn(List.of(taskResponse1));

        ResultActions perform = mockMvc.perform(get("/v1/tracker/apps/{uniqueCode}/tasks", uniqueCode)
                .param("pageNum", String.valueOf(pageNum))
                .param("value", String.valueOf(value))
        );

        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$..taskName").value("TaskName"))
                .andExpect(jsonPath("$..priorityEnums").value("LOW_PRIORITY"))
                .andDo(print());


    }

    @Test
    void createNewTask_Created() throws Exception {
        String uniqueCode = "eu123";
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setTaskName("name");
        Task task = new Task();

        when(taskService.saveTask(uniqueCode,taskDTO)).thenReturn(task);

        ResultActions perform = mockMvc.perform(post("/v1/tracker/apps/{uniqueCode}/createTask", uniqueCode)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskDTO))
        );

        perform.andExpect(status().isCreated())
                .andExpect(content().string("success"));
    }

    @Test
    void createNewTask_AppNotFound() throws Exception {
        String uniqueCode = "eu123";
        TaskDTO task = new TaskDTO();
        task.setTaskName("name");

        when(taskService.saveTask(uniqueCode,task)).thenThrow(new AppNotFoundException("app not found"));

        ResultActions perform = mockMvc.perform(post("/v1/tracker/apps/{uniqueCode}/createTask", uniqueCode)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(task)
        ));

        perform.andExpect(status().isNotFound());
    }

    @Test
    void sortTaskByPriority_Ok_HIGH_PRIORITY() throws Exception {
        String uniqueCode = "eu123";
        String enums = "HIGH_PRIORITY";
        when(taskService.getTasksSortedByPriority(uniqueCode,PriorityEnums.HIGH_PRIORITY, pageNum, value))
                .thenReturn(List.of(taskResponse2,taskResponse1));

        ResultActions perform = mockMvc.perform(get("/v1/tracker/apps/{uniqueCode}/sortTaskByPriority", uniqueCode)
                .param("uniqueCode", uniqueCode)
                .param("pageNum", String.valueOf(pageNum))
                .param("value", String.valueOf(value))
                .contentType(MediaType.APPLICATION_JSON)
                .content(enums)
        );

        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$[0]taskName").value("Test"))
                .andExpect(jsonPath("$[0]priorityEnums").value("HIGH_PRIORITY"))
                .andDo(print());

    }

    @Test
    void sortTaskByDate_Ok_CLOSEST() throws Exception {
        String uniqueCode = "eu123";
        String enums = "CLOSEST";
        when(taskService.getTasksSortedByDate(uniqueCode,DateEnums.CLOSEST, pageNum, value))
                .thenReturn(List.of(taskResponse1,taskResponse2));

        ResultActions perform = mockMvc.perform(get("/v1/tracker/apps/{uniqueCode}/sortTaskByDate", uniqueCode)
                .param("uniqueCode", uniqueCode)
                .param("pageNum", String.valueOf(pageNum))
                .param("value", String.valueOf(value))
                .contentType(MediaType.APPLICATION_JSON)
                .content(enums)
        );

        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$[0]taskName").value("TaskName"))
                .andExpect(jsonPath("$[0]priorityEnums").value("LOW_PRIORITY"))
                .andDo(print());
    }
}