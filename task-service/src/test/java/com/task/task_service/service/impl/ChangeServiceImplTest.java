package com.task.task_service.service.impl;

import com.task.task_service.exceptions.AppNotFoundException;
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
import org.springframework.cache.CacheManager;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ChangeServiceImplTest {

    @InjectMocks
    ChangeServiceImpl changeService;

    @Mock
    RestTemplate restTemplate;

    @Mock
    AppRepository appRepository;


    @Mock
    ChangeRepository changeRepository;

    @Mock
    CacheManager cacheManager;

    private static final String UNIQUE_CODE = "uniqueCode";

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
    void testLoadAppChanges_AppNotFound() {
        when(appRepository.findAppByUniqueCode(UNIQUE_CODE)).thenReturn(Optional.empty());

        assertThrows(AppNotFoundException.class, () -> changeService.loadAppChanges(UNIQUE_CODE));
    }


    @Test
    void testCreateChangeFromJson()  {
        String json = "[{\"title\": \"Test\", \"created_at\": \"2024-07-31T12:00:00Z\"}]";
        List<Change> expectedChanges = Arrays.asList(change);

        List<Change> result = changeService.createChangeFromJson(json);

        assertEquals(expectedChanges.size(), result.size());
        assertEquals(expectedChanges.get(0).getChangeTitle(), result.get(0).getChangeTitle());
    }
}