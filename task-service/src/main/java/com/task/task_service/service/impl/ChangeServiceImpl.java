package com.task.task_service.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.task_service.exceptions.AppNotFoundException;
import com.task.task_service.models.app.App;
import com.task.task_service.models.change.Change;
import com.task.task_service.models.GitHubRequest;
import com.task.task_service.models.change.ChangeResponse;
import com.task.task_service.models.enums.ChangeType;
import com.task.task_service.repository.AppRepository;
import com.task.task_service.repository.ChangeRepository;
import com.task.task_service.service.ChangeService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
public class ChangeServiceImpl implements ChangeService {

    RestTemplate restTemplate;
    AppRepository appRepository;
    ChangeRepository changeRepository;

    static String GITHUB_URL = "https://api.github.com/repos/";
    static String AUTH_TOKEN = "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";

    @Override
    public List<ChangeResponse> getChanges(String uniqueCode) {

        List<Change> changesByAppUniqueCode = changeRepository.getChangesByAppUniqueCode(uniqueCode);
        log.info("result changes: "+changesByAppUniqueCode);
        List<ChangeResponse> collect = changesByAppUniqueCode
                .stream()
                .map(this::mapToChangeResponse)
                .collect(Collectors.toList());
        return collect;

    }

    ChangeResponse mapToChangeResponse(Change change){
        return ChangeResponse.builder()
                .id(change.getId())
                .changeTime(change.getChangeTime())
                .changeTitle(change.getChangeTitle())
                .personWhoAddChange(change.getPersonWhoAddChange())
                .changeType(change.getChangeType())
                .build();
    }

    @Override
    public void loadAppChanges(String uniqueCode) throws AppNotFoundException {
        App app = appRepository.findAppByUniqueCode(uniqueCode)
                .orElseThrow(() -> new AppNotFoundException("app not found"));
        log.info("app: "+app.getName());
        GitHubRequest gitHubRequest = GitHubRequest
                .builder()
                .appName(app.getName())
                .userName(app.getGitHubUserName())
                .build();
        loadChange(app, gitHubRequest, "issues");
        loadChange(app, gitHubRequest, "pulls");

    }

    //TODO create more quickly

    @Override
    public synchronized void loadChange(App app, GitHubRequest gitHubRequest, String changeType) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization","token "+AUTH_TOKEN);

        String url = GITHUB_URL  + gitHubRequest.getUserName() + "/" + gitHubRequest.getAppName()+"/"+changeType;
        HttpEntity<Void> httpEntity = new HttpEntity<>(httpHeaders);
        ResponseEntity<String> exchange = restTemplate.exchange(
                url,
                HttpMethod.GET,
                httpEntity,
                String.class
        );
        log.info("exchange:" + exchange);
        List<Change> changes = createChangeFromJson(exchange.getBody());
        log.info("changes:" + changes);
        List<Change> changeList = addChangeType(changes,changeType);
        log.info("changesList:" + changeList);
        changeList.stream()
                .filter(change -> changeRepository.findChangeByChangeTitle(change.getChangeTitle()) == null)
                .forEach(change -> {
                    change.setAppUniqueCode(app.getUniqueCode());
                    changeRepository.save(change);
                    log.info("successfully saved");
                });
    }


    public List<Change> addChangeType(List<Change> changeList, String changeType) {
        if (changeType.equals("issues")){
            for (Change change : changeList){
                change.setChangeType(ChangeType.ISSUES);
            }
        }else {
            for (Change change : changeList){
                change.setChangeType(ChangeType.PULL_REQUESTS);
            }
        }
        return changeList;
    }

    @Override
    public List<Change> createChangeFromJson(String json) {
        ObjectMapper mapper = new ObjectMapper();
        List<Change> changes = new ArrayList<>();
        try {
            JsonNode jsonNode = mapper.readTree(json);
            for (JsonNode issueNode : jsonNode) {
                String title = issueNode.path("title").asText();
                String createdAt = issueNode.path("created_at").asText();
                JsonNode user = issueNode.path("user");
                String username = user.path("login").asText();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
                LocalDateTime localDateTime = LocalDateTime.parse(createdAt, formatter);
                changes.add(createChange(title,username,localDateTime));

            }
        }catch (JsonProcessingException e){
            e.printStackTrace();
        }
        return changes;
    }

    private Change createChange(String title,String username,LocalDateTime localDateTime){
        Change change = new Change();
        change.setChangeTime(localDateTime);
        change.setPersonWhoAddChange(username);
        change.setChangeTitle(title);
        return change;
    }
}
