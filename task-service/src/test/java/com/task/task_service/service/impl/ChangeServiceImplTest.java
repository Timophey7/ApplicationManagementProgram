package com.task.task_service.service.impl;

import com.task.task_service.exceptions.AppNotFoundException;
import com.task.task_service.models.GitHubRequest;
import com.task.task_service.models.app.App;
import com.task.task_service.models.change.Change;
import com.task.task_service.models.change.ChangeResponse;
import com.task.task_service.models.enums.ChangeType;
import com.task.task_service.repository.AppRepository;
import com.task.task_service.repository.ChangeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ChangeServiceImplTest {

    private static final Logger log = LoggerFactory.getLogger(ChangeServiceImplTest.class);
    @InjectMocks
    ChangeServiceImpl changeService;

    @Mock
    RestTemplate restTemplate;

    @Mock
    AppRepository appRepository;

    @Mock
    ChangeRepository changeRepository;

    private static final String UNIQUE_CODE = "uniqueCode";
    private static final String GITHUB_USER_NAME = "testUser";
    private static final String APP_NAME = "testApp";
    private static final String AUTH_TOKEN = "ghp_testToken";


    Change change;
    ChangeResponse changeResponse;

    @BeforeEach
    void setUp() {
        String createdAt = "2024-07-31T12:00:00Z";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        LocalDateTime localDateTime = LocalDateTime.parse(createdAt, formatter);


        change = new Change();
        change.setId(1);
        change.setChangeType(ChangeType.ISSUES);
        change.setPersonWhoAddChange("TestPerson");
        change.setAppUniqueCode("uniqueCode");
        change.setChangeTime(localDateTime);
        change.setChangeTitle("Test");
        change.setApp(new App());

        changeResponse = new ChangeResponse();
        changeResponse.setId(1);
        changeResponse.setPersonWhoAddChange("TestPerson");
        changeResponse.setChangeTime(localDateTime);
        changeResponse.setChangeType(ChangeType.ISSUES);
        changeResponse.setChangeTitle("Test");
    }

    @Test
    void getChanges() {

        when(changeRepository.getChangesByAppUniqueCode(change.getAppUniqueCode()))
                .thenReturn(List.of(change));

        List<ChangeResponse> changes = changeService.getChanges(change.getAppUniqueCode());

        assertEquals(changes,List.of(changeResponse));
        verify(changeRepository).getChangesByAppUniqueCode(change.getAppUniqueCode());

    }

    @Test
    void mapToChangeResponse() {

        ChangeResponse changeResp = changeService.mapToChangeResponse(change);

        assertEquals(changeResp,changeResponse);

    }

    @Test
    public void testLoadAppChanges_AppNotFound() {
        when(appRepository.findAppByUniqueCode(UNIQUE_CODE)).thenReturn(Optional.empty());

        assertThrows(AppNotFoundException.class, () -> changeService.loadAppChanges(UNIQUE_CODE));
    }

    @Test
    public void testLoadAppChanges_Success() throws AppNotFoundException {
        App app = new App();
        app.setUniqueCode(UNIQUE_CODE);
        app.setName(APP_NAME);
        app.setGitHubUserName(GITHUB_USER_NAME);

        when(appRepository.findAppByUniqueCode(UNIQUE_CODE)).thenReturn(Optional.of(app));

        String jsonResponse = "[{\"title\": \"Issue 1\", \"created_at\": \"2024-07-31T12:00:00Z\"}, {\"title\": \"Issue 2\", \"created_at\": \"2024-07-31T12:01:00Z\"}]";
        ResponseEntity<String> responseEntity = mock(ResponseEntity.class);
        when(responseEntity.getBody()).thenReturn(jsonResponse);
        when(restTemplate.exchange(any(String.class), eq(HttpMethod.GET), any(), eq(String.class))).thenReturn(responseEntity);

        changeService.loadAppChanges(UNIQUE_CODE);

        verify(changeRepository, times(4)).save(any(Change.class));
    }

    @Test
    public void testLoadChange() {
        App app = new App();
        app.setUniqueCode(UNIQUE_CODE);
        app.setName(APP_NAME);
        app.setGitHubUserName(GITHUB_USER_NAME);

        GitHubRequest gitHubRequest = GitHubRequest.builder()
                .appName(app.getName())
                .userName(app.getGitHubUserName())
                .build();

        String jsonResponse = "[{\"title\": \"Issue 1\", \"created_at\": \"2024-07-31T12:00:00Z\"}]";
        ResponseEntity<String> responseEntity = mock(ResponseEntity.class);
        when(responseEntity.getBody()).thenReturn(jsonResponse);
        when(restTemplate.exchange(any(String.class), eq(HttpMethod.GET), any(), eq(String.class))).thenReturn(responseEntity);

        changeService.loadChange(app, gitHubRequest, "issues");

        verify(changeRepository).save(any(Change.class));
    }

    @Test
    public void testAddChangeType() {
        Change change1 = new Change();
        Change change2 = new Change();
        List<Change> changes = Arrays.asList(change1);
        List<Change> changes2 = Arrays.asList(change2);

        List<Change> resultIssues = changeService.addChangeType(changes, "issues");
        List<Change> resultPulls = changeService.addChangeType(changes2, "pulls");
        assertEquals(ChangeType.PULL_REQUESTS, resultPulls.get(0).getChangeType());
        assertEquals(ChangeType.ISSUES,resultIssues.get(0).getChangeType());
    }

    @Test
    public void testCreateChangeFromJson() throws Exception {
        String json = "[{\"title\": \"Test\", \"created_at\": \"2024-07-31T12:00:00Z\"}]";
        List<Change> expectedChanges = Arrays.asList(change);

        List<Change> result = changeService.createChangeFromJson(json);

        assertEquals(expectedChanges.size(), result.size());
        assertEquals(expectedChanges.get(0).getChangeTitle(), result.get(0).getChangeTitle());
    }
}