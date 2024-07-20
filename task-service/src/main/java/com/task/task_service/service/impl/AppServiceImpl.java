package com.task.task_service.service.impl;

import com.task.task_service.models.app.App;
import com.task.task_service.models.app.AppUser;
import com.task.task_service.models.app.CreateAppTrackerDTO;
import com.task.task_service.repository.AppRepository;
import com.task.task_service.repository.AppUserRepository;
import com.task.task_service.service.AppService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
public class AppServiceImpl implements AppService {

    WebClient.Builder webClientBuilder;
    AppRepository appRepository;
    AppUserRepository appUserRepository;

    @Override
    public void checkUsersEmails(List<String> emails) throws UserPrincipalNotFoundException {

        for (String email : emails){

            String result = webClientBuilder.build().post()
                    .uri("http://localhost:8085/v1/auth/userExists")
                    .bodyValue(email)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (result.equals("notExists")){
                throw new UserPrincipalNotFoundException("user not found");
            }

        }

    }

    @Override
    public App createAppTrackerByTrackerDTO(CreateAppTrackerDTO appTrackerDTO) throws UserPrincipalNotFoundException {
        App app = App.builder()
                .id(appTrackerDTO.getAppId())
                .name(appTrackerDTO.getName())
                .gitHubUserName(appTrackerDTO.getGitHubUserName())
                .description(appTrackerDTO.getDescription())
                .build();

        checkUsersEmails(appTrackerDTO.getEmails());


        appRepository.save(app);
        appTrackerDTO.getEmails().stream()
                .forEach(el -> appUserRepository.save(new AppUser(app.getId(),el)));

        return app;
    }


}
