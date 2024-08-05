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

@RestController
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
@RequestMapping("/v1/tracker")
public class TaskController {

    TaskService taskService;

    @PostMapping("/apps/{uniqueCode}/tasks/{taskId}/setCondition")
    public ResponseEntity<?> setTaskCondition(@PathVariable("taskId") int taskId,@RequestBody String condition){
        try {
            taskService.setCondition(taskId, condition);
            return ResponseEntity.ok("success");
        }catch (TaskNotFoundException exception){
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/apps/{uniqueCode}/tasks/{taskId}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable("taskId") int taskId){
        try {
            TaskResponse taskResponseById = taskService.getTaskResponseById(taskId);
            return ResponseEntity.ok(taskResponseById);
        }catch (TaskNotFoundException exception){
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/apps/{uniqueCode}/tasks")
    public ResponseEntity<?> getAllTasksByApp(@PathVariable("uniqueCode") String uniqueCode){
        List<TaskResponse> tasksByApp = taskService.getTasksByApp(uniqueCode);
        return new ResponseEntity<>(
                tasksByApp,
                HttpStatus.OK
        );
    }

    @PostMapping("/apps/{uniqueCode}/createTask")
    public ResponseEntity<?> createNewTask(@PathVariable("uniqueCode") String uniqueCode, @RequestBody Task task){
        try {
            Task createdTask = taskService.saveTask(uniqueCode, task);
            return new ResponseEntity<>(
                    "success",
                    HttpStatus.CREATED
            );
        }catch (AppNotFoundException exception){
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/apps/{uniqueCode}/sortTaskByPriority")
    public ResponseEntity<?> sortTaskByPriority(@PathVariable("uniqueCode") String uniqueCode,@RequestBody String enums){
        PriorityEnums priorityEnums = PriorityEnums.valueOf(enums);
        List<TaskResponse> taskResponseList = taskService.getSortedTasks(uniqueCode, priorityEnums);
        return new ResponseEntity<>(
                taskResponseList,
                HttpStatus.OK
        );
    }

    @GetMapping("/apps/{uniqueCode}/sortTaskByDate")
    public ResponseEntity<?> sortTaskByDate(@PathVariable("uniqueCode") String uniqueCode,@RequestBody String enums){
        DateEnums dateEnums = DateEnums.valueOf(enums);
        List<TaskResponse> taskResponseList = taskService.getSortedTaskByDate(uniqueCode, dateEnums);
        return new ResponseEntity<>(
                taskResponseList,
                HttpStatus.OK
        );
    }

}
