package com.notservice.models;

import lombok.Data;

import java.io.Serializable;

@Data
public class MessageResponse implements Serializable {
    private String email;
    private int app_id;
}
