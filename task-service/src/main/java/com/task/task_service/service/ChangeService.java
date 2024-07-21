package com.task.task_service.service;

import com.task.task_service.exceptions.AppNotFoundException;
import com.task.task_service.models.app.App;
import com.task.task_service.models.Change;
import com.task.task_service.models.GitHubRequest;

import java.util.List;

public interface ChangeService {


    void getChangesByApp(String uniqueCode) throws AppNotFoundException;

    void getChange(App app, GitHubRequest gitHubRequest, String changeType);

    List<Change> createChangeFromJson(String json);

}
