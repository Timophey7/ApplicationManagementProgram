package com.task.task_service.models.enums;

public enum PriorityEnums {
    LOW_PRIORITY(0),
    MIDDLE_PRIORITY(1),
    HIGH_PRIORITY(2);

    private final int priorityLevel;

    PriorityEnums(int priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    public int getPriorityLevel() {
        return priorityLevel;
    }
}
