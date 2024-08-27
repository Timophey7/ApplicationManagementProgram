package com.task.task_service.models.enums;

public enum ChangeType {

    ISSUES("issues"),
    PULL_REQUESTS("pulls");

    private final String type;

    ChangeType(String changeType) {
        this.type = changeType;
    }
}
