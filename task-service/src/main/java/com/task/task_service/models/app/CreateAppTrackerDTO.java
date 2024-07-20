package com.task.task_service.models.app;

import lombok.Data;

import java.util.List;

@Data
public class CreateAppTrackerDTO {

    int appId;
    String name;
    String gitHubUserName;
    String description;
    List<String> emails;

}
