package com.task.task_service.models.app;

import lombok.Data;

import java.util.List;

@Data
public class CreateAppTrackerDTO {

    String name;
    String gitHubUserName;
    String description;
    List<String> emails;

}
