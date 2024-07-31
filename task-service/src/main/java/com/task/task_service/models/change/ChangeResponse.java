package com.task.task_service.models.change;

import com.task.task_service.models.enums.ChangeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ChangeResponse {

    int id;
    String changeTitle;
    LocalDateTime changeTime;
    String personWhoAddChange;
    ChangeType changeType;

}
