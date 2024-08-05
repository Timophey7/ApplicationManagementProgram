package com.task.task_service.controller;

import com.task.task_service.exceptions.AppNotFoundException;
import com.task.task_service.models.change.ChangeResponse;
import com.task.task_service.service.ChangeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;


import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.mockito.Mockito.*;

@WebMvcTest(controllers = GitHubIntegrationController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class GitHubIntegrationControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ChangeService changeService;


    @BeforeEach
    void setUp() {
    }

    @Test
    void getChanges_Success() throws Exception {
        String uniqueCode = "uniqueCode";
        ChangeResponse changeResponse = new ChangeResponse();
        changeResponse.setId(1);
        changeResponse.setChangeTitle("TestTitle");

        doNothing().when(changeService).loadAppChanges(uniqueCode);
        when(changeService.getChanges(uniqueCode)).thenReturn(List.of(changeResponse));

        ResultActions perform = mockMvc.perform(get("/v1/tracker/apps/{uniqueCode}/getChanges",uniqueCode));

        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$..id").value(1))
                .andExpect(jsonPath("$..changeTitle").value("TestTitle"))
                .andDo(print());

    }

    @Test
    void getChanges_NotFound() throws Exception {
        String uniqueCode = "uniqueCode";
        ChangeResponse changeResponse = new ChangeResponse();
        changeResponse.setId(1);
        changeResponse.setChangeTitle("TestTitle");

        doThrow(new AppNotFoundException("app not found")).when(changeService).loadAppChanges(uniqueCode);
        when(changeService.getChanges(uniqueCode)).thenReturn(List.of(changeResponse));

        ResultActions perform = mockMvc.perform(get("/v1/tracker/apps/{uniqueCode}/getChanges",uniqueCode)
                .param("uniqueCode",uniqueCode)
        );

        perform.andExpect(status().isNotFound());

    }



}