package com.task.task_service.service.impl;

import com.task.task_service.exceptions.AppNotFoundException;
import com.task.task_service.models.Change;
import com.task.task_service.models.GitHubRequest;
import com.task.task_service.models.app.App;
import com.task.task_service.repository.AppRepository;
import com.task.task_service.repository.ChangeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

class ChangeServiceImplTest {


    @Mock
    RestTemplate restTemplate;
    @Mock
    AppRepository appRepository;
    @Mock
    ChangeRepository changeRepository;

    @InjectMocks
    ChangeServiceImpl changeService;

    App app;
    GitHubRequest gitHubRequest;
    String GITHUB_URL;
    String AUTH_TOKEN;

    @BeforeEach
    void setUp() {
        GITHUB_URL = "https://api.github.com/repos/";
        AUTH_TOKEN = "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";
        app = App.builder()
                .id(1)
                .uniqueCode("werty")
                .gitHubUserName("userName")
                .name("APPNAME")
                .build();

        gitHubRequest = GitHubRequest.builder()
                .userName("userName")
                .appName("APPNAME")
                .build();
    }

    @Test
    void getChangesByApp() throws AppNotFoundException {
        String uniqueCode = "werty";
        when(appRepository.findAppByUniqueCode(uniqueCode))
                .thenReturn(Optional.of(app));

        changeService.getChangesByApp(uniqueCode);

        verify(changeService, times(2)).getChange(app, gitHubRequest, anyString());
        verify(appRepository).findAppByUniqueCode(uniqueCode);
    }

    @Test
    void getChangeShouldGetChangesFromGitHubAndSave() {
        String changeType = "issues";
        String jsonResponse = "[{\"changeTitle\":\"Test Change\", \"created_at\":\"2023-10-26T12:34:56Z\"}]";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "token " + AUTH_TOKEN);
        HttpEntity<Void> httpEntity = new HttpEntity<>(httpHeaders);
        ResponseEntity<String> exchange = ResponseEntity.ok(jsonResponse);

        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class))).thenReturn(exchange);
        when(changeRepository.findChangeByChangeTitle(anyString())).thenReturn(null);

        changeService.getChange(app, gitHubRequest, changeType);

        verify(restTemplate, times(1)).exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class));
        verify(changeRepository, times(1)).findChangeByChangeTitle("Test Change");
        verify(changeRepository, times(1)).save(any(Change.class));
        verify(appRepository, times(1)).save(app);

    }

    @Test
    void addChangeType() {
    }

    @Test
    void createChangeFromJson() {
    }
}