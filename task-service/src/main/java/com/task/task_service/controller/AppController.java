package com.task.task_service.controller;

import com.task.task_service.models.app.App;
import com.task.task_service.models.app.CreateAppTrackerDTO;
import com.task.task_service.service.AppService;
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
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
@RequestMapping("/v1/tracker")
public class AppController {

    AppService appService;


    @PostMapping("/createAppTracker")
    public ResponseEntity<?> createAppTracker(@Valid @RequestBody CreateAppTrackerDTO appTrackerDTO, BindingResult result){
        if (result.hasErrors()){
            return ResponseEntity.badRequest().build();
        }

        try {
            App appTrackerByTrackerDTO = appService.createAppTrackerByTrackerDTO(appTrackerDTO);
            return new ResponseEntity<>(
                    appTrackerByTrackerDTO,
                    HttpStatus.CREATED
            );
        }catch (UserPrincipalNotFoundException exception){
            return ResponseEntity.badRequest().body("user not exists");
        }
    }

}
