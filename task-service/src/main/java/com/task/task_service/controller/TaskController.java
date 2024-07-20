package com.task.task_service.controller;

import com.task.task_service.exceptions.AppNotFoundException;
import com.task.task_service.exceptions.TaskNotFoundException;
import com.task.task_service.models.enums.DateEnums;
import com.task.task_service.models.enums.PriorityEnums;
import com.task.task_service.models.tasks.Task;
import com.task.task_service.models.tasks.TaskResponse;
import com.task.task_service.service.TaskService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
@RequestMapping("/v1/tracker")
public class TaskController {

    TaskService taskService;

    @PostMapping("/apps/{appId}/tasks/{taskId}/setCondition")
    public ResponseEntity<?> setTaskCondition(@PathVariable("taskId") int taskId,@RequestBody String condition){
        try {
            taskService.setCondition(taskId, condition);
            return ResponseEntity.ok("success");
        }catch (TaskNotFoundException exception){
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/apps/{appId}/tasks/{taskId}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable("taskId") int taskId){
        try {
            TaskResponse taskResponseById = taskService.getTaskResponseById(taskId);
            return ResponseEntity.ok(taskResponseById);
        }catch (TaskNotFoundException exception){
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/apps/{appId}/tasks")
    public ResponseEntity<?> getAllTasksByApp(@PathVariable("appId") int appId){
        List<TaskResponse> tasksByApp = taskService.getTasksByApp(appId);
        return new ResponseEntity<>(
                tasksByApp,
                HttpStatus.OK
        );
    }

    @PostMapping("/apps/{appId}/createTask")
    public ResponseEntity<?> createNewTask(@PathVariable("appId") int appId, @RequestBody Task task){
        try {
            Task createdTask = taskService.saveTask(appId, task);
            return new ResponseEntity<>(
                    "success",
                    HttpStatus.CREATED
            );
        }catch (AppNotFoundException exception){
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/apps/{appId}/sortTaskByPriority")
    public ResponseEntity<?> sortTaskByPriority(@PathVariable("appId") int appId,@RequestBody String enums){
        PriorityEnums priorityEnums = PriorityEnums.valueOf(enums);
        List<TaskResponse> taskResponseList = taskService.getSortedTasks(appId, priorityEnums)
                .stream()
                .map(taskService::mapToTaskResponse)
                .collect(Collectors.toList());

        return new ResponseEntity<>(
                taskResponseList,
                HttpStatus.OK
        );
    }

    @GetMapping("/apps/{appId}/sortTaskByDate")
    public ResponseEntity<?> sortTaskByDate(@PathVariable("appId") int appId,@RequestBody String enums){
        DateEnums dateEnums = DateEnums.valueOf(enums);
        List<TaskResponse> taskResponseList = taskService.getSortedTaskByDate(appId, dateEnums)
                .stream()
                .map(taskService::mapToTaskResponse)
                .collect(Collectors.toList());


        return new ResponseEntity<>(
                taskResponseList,
                HttpStatus.OK
        );
    }

}
