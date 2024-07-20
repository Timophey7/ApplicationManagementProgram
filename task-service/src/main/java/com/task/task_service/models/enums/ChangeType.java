package com.task.task_service.models.enums;

public enum ChangeType {

    ISSUES("issues"),
    PULL_REQUESTS("pulls");

    private final String changeType;

    ChangeType(String changeType) {
        this.changeType = changeType;
    }
}
