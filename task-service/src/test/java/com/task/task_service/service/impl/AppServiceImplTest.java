package com.task.task_service.service.impl;

import com.task.task_service.models.MessageResponse;
import com.task.task_service.models.app.App;
import com.task.task_service.models.app.AppUser;
import com.task.task_service.models.app.CreateAppTrackerDTO;
import com.task.task_service.repository.AppRepository;
import com.task.task_service.repository.AppUserRepository;
import com.task.task_service.service.utils.UniqueCodeGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AppServiceImplTest {
    @InjectMocks
    private AppServiceImpl appService;

    @Mock
    private UniqueCodeGenerator codeGenerator;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private AppRepository appRepository;

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private KafkaTemplate<String, MessageResponse> kafkaTemplate;

    @Value("${security-service.host}")
    private String securityHost;


    @BeforeEach
    public void setUp() {

    }

    @Test
    public void testCreateAppTrackerByTrackerDTO() throws UserPrincipalNotFoundException {
        CreateAppTrackerDTO appTrackerDTO = new CreateAppTrackerDTO();
        appTrackerDTO.setAppId(1);
        appTrackerDTO.setName("Test App");
        appTrackerDTO.setGitHubUserName("testuser");
        appTrackerDTO.setDescription("Test App Description");
        appTrackerDTO.setEmails(Arrays.asList("user1@example.com", "user2@example.com"));


        WebClient mockWebClient = mock(WebClient.class);
        WebClient.Builder mockBuilder = mock(WebClient.Builder.class);
        WebClient.RequestHeadersUriSpec mockUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestBodyUriSpec mockRequestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.ResponseSpec mockResponseSpec = mock(WebClient.ResponseSpec.class);

        when(mockWebClient.post()).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.uri("http://"+securityHost+":8085/v1/auth/userExists"))
                .thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.bodyValue(any(String.class))).thenReturn(mockUriSpec);
        when(mockUriSpec.retrieve()).thenReturn(mockResponseSpec);
        when(mockResponseSpec.bodyToMono(String.class)).thenReturn(Mono.just("exists"));

        when(webClientBuilder.build()).thenReturn(mockWebClient);

        App result = appService.createAppTrackerByTrackerDTO(appTrackerDTO);

        verify(appRepository).save(any(App.class));
        verify(appUserRepository, times(2)).save(any(AppUser.class));
        verify(kafkaTemplate, times(2)).send(any(String.class), any(MessageResponse.class));
        assertEquals("Test App", result.getName());
    }

    @Test
    public void testCheckUsersEmails_UserNotFound() {
        List<String> emails = Arrays.asList("user3@example.com");

        WebClient mockWebClient = mock(WebClient.class);
        WebClient.Builder mockBuilder = mock(WebClient.Builder.class);
        WebClient.RequestHeadersUriSpec mockUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestBodyUriSpec mockRequestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.ResponseSpec mockResponseSpec = mock(WebClient.ResponseSpec.class);

        when(mockWebClient.post()).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.uri("http://"+securityHost+":8085/v1/auth/userExists"))
                .thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.bodyValue(any(String.class))).thenReturn(mockUriSpec);
        when(mockUriSpec.retrieve()).thenReturn(mockResponseSpec);
        when(mockResponseSpec.bodyToMono(String.class)).thenReturn(Mono.just("notExists"));

        when(webClientBuilder.build()).thenReturn(mockWebClient);

        assertThrows(UserPrincipalNotFoundException.class, () -> {
            appService.checkUsersEmails(emails, 1);
        });
    }

    @Test
    public void testSendMessagesToUsers() {
        List<String> emails = Arrays.asList("user1@example.com", "user2@example.com");
        int appId = 1;

        appService.sendMessagesToUsers(emails, appId);

        verify(kafkaTemplate, times(2)).send(any(String.class), any(MessageResponse.class));
    }
}