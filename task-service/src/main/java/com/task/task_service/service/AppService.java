package com.task.task_service.service;


import com.task.task_service.exceptions.AppAlreadyExistsException;
import com.task.task_service.models.app.App;
import com.task.task_service.models.app.CreateAppTrackerDTO;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.List;

public interface AppService {

    void sendMessagesToUsers(List<String> emails, String uniqueCode);

    void checkUsersEmails(List<String> emails) throws UserPrincipalNotFoundException;

    App createAppTrackerByTrackerDTO(CreateAppTrackerDTO createAppTrackerDTO) throws UserPrincipalNotFoundException, AppAlreadyExistsException;

}
