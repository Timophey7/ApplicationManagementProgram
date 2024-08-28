package com.task.task_service.controller;

import com.task.task_service.exceptions.AppNotFoundException;
import com.task.task_service.exceptions.TaskAlreadyExistsException;
import com.task.task_service.exceptions.TaskNotFoundException;
import com.task.task_service.models.enums.DateEnums;
import com.task.task_service.models.enums.PriorityEnums;
import com.task.task_service.models.tasks.TaskDTO;
import com.task.task_service.models.tasks.TaskResponse;
import com.task.task_service.service.TaskService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequestMapping("/v1/tracker")
public class TaskController {

    TaskService taskService;

    @PostMapping("/apps/{uniqueCode}/tasks/{taskId}/setCondition")
    public ResponseEntity<String> setTaskCondition(
            @PathVariable("uniqueCode") String uniqueCode,
            @PathVariable("taskId") int taskId,
            @RequestBody String condition
    ) {
        try {
            taskService.setCondition(taskId, condition);
            return ResponseEntity.ok("success");
        } catch (TaskNotFoundException exception) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/apps/{uniqueCode}/tasks/{taskId}")
    public ResponseEntity<TaskResponse> getTaskById(
            @PathVariable("uniqueCode") String uniqueCode,
            @PathVariable("taskId") int taskId
    ) {
        try {
            TaskResponse taskResponseById = taskService.getTaskResponseById(taskId);
            return ResponseEntity.ok(taskResponseById);
        } catch (TaskNotFoundException exception) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/apps/{uniqueCode}/tasks")
    public ResponseEntity<List<TaskResponse>> getAllTasksByApp(
            @PathVariable("uniqueCode") String uniqueCode,
            @RequestParam("pageNum") int pageNum,
            @RequestParam("value") int value

    ) {
        try {
            List<TaskResponse> tasksByApp = taskService.getTasksByApp(uniqueCode, pageNum, value);
            return new ResponseEntity<>(
                    tasksByApp,
                    HttpStatus.OK
            );
        } catch (TaskNotFoundException exception) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/apps/{uniqueCode}/createTask")
    public ResponseEntity<String> createNewTask(@PathVariable("uniqueCode") String uniqueCode, @RequestBody TaskDTO task) {
        try {
            taskService.saveTask(uniqueCode, task);
            return new ResponseEntity<>(
                    "success",
                    HttpStatus.CREATED
            );
        } catch (AppNotFoundException exception) {
            return ResponseEntity.notFound().build();
        } catch (TaskAlreadyExistsException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }

    @GetMapping("/apps/{uniqueCode}/sortTaskByPriority")
    public ResponseEntity<List<TaskResponse>> sortTaskByPriority(
            @PathVariable("uniqueCode") String uniqueCode,
            @RequestBody String enums,
            @RequestParam("pageNum") int pageNum,
            @RequestParam("value") int value
    ) {
        try {
            PriorityEnums priorityEnums = PriorityEnums.valueOf(enums);
            List<TaskResponse> taskResponseList = taskService.getTasksSortedByPriority(uniqueCode, priorityEnums, pageNum, value);
            return new ResponseEntity<>(
                    taskResponseList,
                    HttpStatus.OK
            );
        } catch (TaskNotFoundException exception) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/apps/{uniqueCode}/sortTaskByDate")
    public ResponseEntity<List<TaskResponse>> sortTaskByDate(
            @PathVariable("uniqueCode") String uniqueCode,
            @RequestBody String enums,
            @RequestParam("pageNum") int pageNum,
            @RequestParam("value") int value
    ) {
        try {
            DateEnums dateEnums = DateEnums.valueOf(enums);
            List<TaskResponse> taskResponseList = taskService.getTasksSortedByDate(uniqueCode, dateEnums, pageNum, value);
            return new ResponseEntity<>(
                    taskResponseList,
                    HttpStatus.OK
            );
        } catch (TaskNotFoundException exception) {
            return ResponseEntity.notFound().build();
        }
    }

}
