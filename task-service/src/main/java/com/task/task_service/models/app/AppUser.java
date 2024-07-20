package com.task.task_service.models.app;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "app_users")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(name = "app_id")
    int appId;

    @Column(name = "user_email")
    String userEmail;

    public AppUser(int appId, String userEmail) {
        this.appId = appId;
        this.userEmail = userEmail;
    }
}

