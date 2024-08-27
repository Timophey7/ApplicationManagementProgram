package com.task.task_service.service.impl;

import com.task.task_service.exceptions.AppAlreadyExistsException;
import com.task.task_service.models.MessageResponse;
import com.task.task_service.models.app.App;
import com.task.task_service.models.app.AppUser;
import com.task.task_service.models.app.CreateAppTrackerDTO;
import com.task.task_service.models.enums.Role;
import com.task.task_service.repository.AppRepository;
import com.task.task_service.repository.AppUserRepository;
import com.task.task_service.service.AppService;
import com.task.task_service.service.utils.UniqueCodeGenerator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = false,level = AccessLevel.PRIVATE)
public class AppServiceImpl implements AppService {

    @Value("${security-service.host}")
    String securityHost;

    final WebClient.Builder webClientBuilder;
    final AppRepository appRepository;
    final AppUserRepository appUserRepository;
    final KafkaTemplate<String, MessageResponse> kafkaTemplate;


    @Override
    public synchronized App createAppTrackerByTrackerDTO(CreateAppTrackerDTO appTrackerDTO) throws UserPrincipalNotFoundException, AppAlreadyExistsException {
        if (appAlreadyExists(appTrackerDTO)){
            throw new AppAlreadyExistsException("app already exists");
        }
        App app = mapToApp(appTrackerDTO);
        checkUsersEmails(appTrackerDTO.getEmails());
        sendMessagesToUsers(appTrackerDTO.getEmails(),app.getUniqueCode());
        List<String> emails = appTrackerDTO.getEmails();
        appRepository.save(app);
        saveUsersInApp(app,emails);
        return app;
    }

    public boolean appAlreadyExists(CreateAppTrackerDTO createAppTrackerDTO){
        App app = appRepository.findAppByNameAndGitHubUserName(createAppTrackerDTO.getName(), createAppTrackerDTO.getGitHubUserName())
                .orElse(null);
        return app != null ;
    }

    private void saveUsersInApp(App app, List<String> emails){
        emails.stream()
                .forEach(el -> {
                    if (emails.stream().findFirst().orElse(null).equals(el)){
                        appUserRepository.save(new AppUser(app.getId(),app.getUniqueCode(),el, Role.ADMIN));
                    }else {
                        appUserRepository.save(new AppUser(app.getId(),app.getUniqueCode(), el, Role.DEVELOPER));
                    }
                });
    }

    private App mapToApp(CreateAppTrackerDTO appTrackerDTO){
        return  App.builder()
                .name(appTrackerDTO.getName())
                .uniqueCode(UniqueCodeGenerator.generateCode(appTrackerDTO.getName()))
                .gitHubUserName(appTrackerDTO.getGitHubUserName())
                .description(appTrackerDTO.getDescription())
                .build();
    }


    @Override
    public void checkUsersEmails(List<String> emails) throws UserPrincipalNotFoundException {

        for (String email : emails){
            String result = webClientBuilder.build().post()
                    .uri("http://"+securityHost+":8085/v1/auth/userExists")
                    .bodyValue(email)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            if (Objects.equals(result, "notExists")) {
                throw new UserPrincipalNotFoundException("user not found");
            }
        }

    }


    @Override
    public void sendMessagesToUsers(List<String> emails,String uniqueCode) {
        for (String email : emails){
            try {
                sendMessageToKafkaTopic(email, uniqueCode);
            }catch (Exception exception){
                exception.printStackTrace();
            }
        }
    }

    public void sendMessageToKafkaTopic(String email,String uniqueCode){
        MessageResponse messageResponse = new MessageResponse();
        messageResponse.setEmail(email);
        messageResponse.setUniqueCode(uniqueCode);
        kafkaTemplate.send("invite",messageResponse);
    }


}
