package com.task.task_service.models.app;

import com.task.task_service.models.enums.Role;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@AllArgsConstructor
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

    @Column(name = "app_unique_code")
    String appUniqueCode;

    @Column(name = "user_email",unique = false)
    String userEmail;

    @Enumerated(EnumType.STRING)
    Role userRole;

    public AppUser(int appId,String appUniqueCode, String userEmail,Role userRole) {
        this.appId = appId;
        this.appUniqueCode = appUniqueCode;
        this.userEmail = userEmail;
        this.userRole = userRole;
    }
}

