package com.task.task_service.exceptions;

import java.util.function.Supplier;

public class AppNotFoundException extends Exception {

    public AppNotFoundException(String message) {
        super(message);
    }
}
