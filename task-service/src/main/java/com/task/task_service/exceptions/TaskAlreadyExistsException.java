package com.task.task_service.exceptions;

public class TaskAlreadyExistsException extends Exception{
    public TaskAlreadyExistsException(String message) {
        super(message);
    }
}
