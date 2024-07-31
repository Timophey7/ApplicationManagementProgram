package com.task.task_service.service;

import com.task.task_service.exceptions.AppNotFoundException;
import com.task.task_service.models.app.App;
import com.task.task_service.models.change.Change;
import com.task.task_service.models.GitHubRequest;
import com.task.task_service.models.change.ChangeResponse;

import java.util.List;

public interface ChangeService {

    List<ChangeResponse> getChanges(String uniqueCode);

    void loadAppChanges(String uniqueCode) throws AppNotFoundException;

    void loadChange(App app, GitHubRequest gitHubRequest, String changeType);

    List<Change> createChangeFromJson(String json);

}
