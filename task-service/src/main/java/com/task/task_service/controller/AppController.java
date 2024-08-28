package com.task.task_service.controller;

import com.task.task_service.exceptions.AppAlreadyExistsException;
import com.task.task_service.models.app.App;
import com.task.task_service.models.app.CreateAppTrackerDTO;
import com.task.task_service.service.AppService;
import io.github.bucket4j.Bucket;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.nio.file.attribute.UserPrincipalNotFoundException;


@RestController
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequestMapping("/v1/tracker")
public class AppController {

    AppService appService;
    Bucket bucket;

    @PostMapping("/createAppTracker")
    public ResponseEntity<Object> createAppTracker(@Valid @RequestBody CreateAppTrackerDTO appTrackerDTO, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }
        if (bucket.tryConsume(1)) {
            try {
                App appTrackerByTrackerDTO = appService.createAppTrackerByTrackerDTO(appTrackerDTO);
                return new ResponseEntity<>(
                        appTrackerByTrackerDTO,
                        HttpStatus.CREATED
                );
            } catch (UserPrincipalNotFoundException exception) {
                return ResponseEntity.badRequest().body("user not exists");
            } catch (AppAlreadyExistsException exception) {
                return ResponseEntity.badRequest().body(exception.getMessage());
            }
        } else {
            return new ResponseEntity<>(
                    "too many requests",
                    HttpStatus.TOO_MANY_REQUESTS
            );
        }
    }

}
