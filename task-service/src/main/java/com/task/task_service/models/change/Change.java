package com.task.task_service.models.change;

import com.task.task_service.models.app.App;
import com.task.task_service.models.enums.ChangeType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "app_changes")
public class Change {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    String changeTitle;
    LocalDateTime changeTime;
    String personWhoAddChange;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_id")
    App app;
    String appUniqueCode;
    @Enumerated(EnumType.STRING)
    ChangeType changeType;

}
