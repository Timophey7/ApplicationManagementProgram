package com.task.task_service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.task.task_service.exceptions.AppNotFoundException;
import com.task.task_service.models.GitHubRequest;
import com.task.task_service.service.ChangeService;
import com.task.task_service.service.TaskService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/tracker")
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
public class GitHubIntegrationController {

    ChangeService changeService;

    @GetMapping("/apps/{appId}/getChanges")
    public ResponseEntity<?> getChanges(@PathVariable("appId") int appId){
        try {
            changeService.getChangesByApp(appId);
            return ResponseEntity.ok("success");
        }catch (AppNotFoundException exception){
            return ResponseEntity.notFound().build();
        }

    }



}
