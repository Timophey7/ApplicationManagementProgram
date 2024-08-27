package com.task.task_service.models;

import lombok.Data;


import java.io.Serializable;

@Data
public class MessageResponse implements Serializable {
    private String email;
    private String uniqueCode;
}
