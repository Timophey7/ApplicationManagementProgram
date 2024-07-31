package com.task.task_service.service.impl;

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

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = false,level = AccessLevel.PRIVATE)
public class AppServiceImpl implements AppService {

    @Value("${security-service.host}")
    private String securityHost;

    final UniqueCodeGenerator codeGenerator;
    final WebClient.Builder webClientBuilder;
    final AppRepository appRepository;
    final AppUserRepository appUserRepository;
    final KafkaTemplate<String, MessageResponse> kafkaTemplate;

    @Override
    public void sendMessagesToUsers(List<String> emails, int appId) {
        for (String email : emails){
            try {
                sendMessageToKafkaTopic(email, appId);
            }catch (Exception exception){
                exception.printStackTrace();
            }
        }
    }

    @Override
    public void checkUsersEmails(List<String> emails, int appId) throws UserPrincipalNotFoundException {

        for (String email : emails){
            String result = webClientBuilder.build().post()
                    .uri("http://"+securityHost+":8085/v1/auth/userExists")
                    .bodyValue(email)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            if (result.equals("notExists")){
                throw new UserPrincipalNotFoundException("user not found");
            }
        }

    }

    public void sendMessageToKafkaTopic(String email,int appId){
        MessageResponse messageResponse = new MessageResponse();
        messageResponse.setEmail(email);
        messageResponse.setApp_id(appId);
        kafkaTemplate.send("invite",messageResponse);
    }

    @Override
    public App createAppTrackerByTrackerDTO(CreateAppTrackerDTO appTrackerDTO) throws UserPrincipalNotFoundException {
        App app = App.builder()
                .id(appTrackerDTO.getAppId())
                .name(appTrackerDTO.getName())
                .uniqueCode(codeGenerator.generateCode(appTrackerDTO.getName()))
                .gitHubUserName(appTrackerDTO.getGitHubUserName())
                .description(appTrackerDTO.getDescription())
                .build();

        checkUsersEmails(appTrackerDTO.getEmails(),appTrackerDTO.getAppId());

        sendMessagesToUsers(appTrackerDTO.getEmails(),appTrackerDTO.getAppId());

        List<String> emails = appTrackerDTO.getEmails();

        appRepository.save(app);
        emails.stream()
                .forEach(el -> {
                    if (emails.stream().findFirst().orElse(null) == el){
                        appUserRepository.save(new AppUser(app.getId(),el, Role.ADMIN));
                    }else {
                        appUserRepository.save(new AppUser(app.getId(), el, Role.DEVELOPER));
                    }
                });

        return app;
    }


}
