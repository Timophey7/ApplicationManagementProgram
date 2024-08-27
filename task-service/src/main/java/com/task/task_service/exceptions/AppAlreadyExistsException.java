package com.task.task_service.exceptions;

public class AppAlreadyExistsException extends Exception{

    public AppAlreadyExistsException(String message) {
        super(message);
    }
}
