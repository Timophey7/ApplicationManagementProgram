package com.task.task_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.task_service.models.app.App;
import com.task.task_service.models.app.CreateAppTrackerDTO;
import com.task.task_service.service.AppService;
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

import java.nio.file.attribute.UserPrincipalNotFoundException;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.mockito.Mockito.*;

@WebMvcTest(controllers = AppController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class AppControllerTest {

    @Autowired
    MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    private AppService appService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void createAppTracker_Created() throws Exception {

        CreateAppTrackerDTO appTrackerDTO = new CreateAppTrackerDTO();
        appTrackerDTO.setAppId(1);
        appTrackerDTO.setName("Test");
        appTrackerDTO.setGitHubUserName("TestUserName");
        appTrackerDTO.setDescription("description");

        App app = new App();
        app.setId(1);
        app.setName("Test");
        app.setUniqueCode("uniqueCode");

        when(appService.createAppTrackerByTrackerDTO(appTrackerDTO)).thenReturn(app);

        ResultActions perform = mockMvc.perform(post("/v1/tracker/createAppTracker")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(appTrackerDTO))
        );

        perform.andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test"))
                .andExpect(jsonPath("$.uniqueCode").value("uniqueCode"))
                .andDo(print());

    }

    @Test
    void createAppTracker_BadRequest_UserNotExists() throws Exception {

        CreateAppTrackerDTO appTrackerDTO = new CreateAppTrackerDTO();
        appTrackerDTO.setAppId(1);
        appTrackerDTO.setName("Test");
        appTrackerDTO.setGitHubUserName("TestUserName");
        appTrackerDTO.setDescription("description");

        App app = new App();
        app.setId(1);
        app.setName("Test");
        app.setUniqueCode("uniqueCode");

        when(appService.createAppTrackerByTrackerDTO(appTrackerDTO))
                .thenThrow(new UserPrincipalNotFoundException("user not exists"));

        ResultActions perform = mockMvc.perform(post("/v1/tracker/createAppTracker")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(appTrackerDTO))
        );

        perform.andExpect(status().isBadRequest())
                .andExpect(content().string("user not exists"))
                .andDo(print());

    }
}