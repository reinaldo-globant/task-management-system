package com.taskmanagement.taskbackend.controller;

import com.taskmanagement.taskbackend.model.Task;
import com.taskmanagement.taskbackend.model.TaskStatus;
import com.taskmanagement.taskbackend.payload.request.TaskRequest;
import com.taskmanagement.taskbackend.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
@Tag(name = "Task Management", description = "Task management API endpoints")
@PreAuthorize("isAuthenticated()")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }
    
    private String getCurrentUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (String)authentication.getPrincipal();       
    }

    @Operation(summary = "Get all tasks", description = "Retrieves a list of all tasks regardless of status or owner")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved all tasks",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = Task.class)))
    })
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        List<Task> tasks = taskService.getAllTasks();
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    @Operation(summary = "Get task by ID", description = "Retrieves a specific task by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the task",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = Task.class))),
        @ApiResponse(responseCode = "404", description = "Task not found", 
                content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(
            @Parameter(description = "ID of the task to be retrieved", required = true) 
            @PathVariable Long id) {
        return taskService.getTaskById(id)
                .map(task -> new ResponseEntity<>(task, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Task>> getTasksByStatus(@PathVariable TaskStatus status) {
        List<Task> tasks = taskService.getTasksByStatus(status);
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    @GetMapping("/owner/{ownerUsername}")
    public ResponseEntity<List<Task>> getTasksByOwner(@PathVariable String ownerUsername) {
        List<Task> tasks = taskService.getTasksByOwnerUsername(ownerUsername);
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }
    
    @GetMapping("/my-tasks")
    public ResponseEntity<List<Task>> getMyTasks() {
        String currentUsername = getCurrentUserName();
        List<Task> tasks = taskService.getTasksByOwnerUsername(currentUsername);
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    @GetMapping("/status/{status}/owner/{ownerUsername}")
    public ResponseEntity<List<Task>> getTasksByStatusAndOwner(
            @PathVariable TaskStatus status,
            @PathVariable String ownerUsername) {
        List<Task> tasks = taskService.getTasksByStatusAndOwnerUsername(status, ownerUsername);
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }
    
    @GetMapping("/my-tasks/status/{status}")
    public ResponseEntity<List<Task>> getMyTasksByStatus(@PathVariable TaskStatus status) {
        String currentUsername = getCurrentUserName();
        List<Task> tasks = taskService.getTasksByStatusAndOwnerUsername(status, currentUsername);
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    @Operation(summary = "Create a new task", description = "Creates a new task with the provided information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Task successfully created",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = Task.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input", 
                content = @Content)
    })
    @PostMapping
    public ResponseEntity<Task> createTask(
            @Parameter(description = "Task object to be created", required = true)
            @Valid @RequestBody TaskRequest taskRequest) {
        String currentUser = getCurrentUserName();
        Task createdTask = taskService.createTask(taskRequest, currentUser);
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }

    @Operation(summary = "Update a task", description = "Updates an existing task with the provided information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task successfully updated",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = Task.class))),
        @ApiResponse(responseCode = "404", description = "Task not found", 
                content = @Content),
        @ApiResponse(responseCode = "403", description = "Not authorized to update this task", 
                content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(
            @Parameter(description = "ID of the task to be updated", required = true)
            @PathVariable Long id, 
            @Parameter(description = "Updated task information", required = true)
            @Valid @RequestBody TaskRequest taskRequest) {
        
        String currentUsername = getCurrentUserName();
        
        return taskService.getTaskById(id)
                .map(existingTask -> {
                    // Check if the user is the owner of the task
                    if (!existingTask.getOwner().equals(currentUsername)) {
                        return new ResponseEntity<Task>(HttpStatus.FORBIDDEN);
                    }
                    
                    existingTask.setTitle(taskRequest.getTitle());
                    existingTask.setDescription(taskRequest.getDescription());
                    existingTask.setStatus(taskRequest.getStatus());
                    
                    Task updatedTask = taskService.saveTask(existingTask);
                    return new ResponseEntity<>(updatedTask, HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Delete a task", description = "Deletes a task by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Task successfully deleted"),
        @ApiResponse(responseCode = "404", description = "Task not found"),
        @ApiResponse(responseCode = "403", description = "Not authorized to delete this task")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @Parameter(description = "ID of the task to be deleted", required = true)
            @PathVariable Long id) {
        
        String currentUsername = getCurrentUserName();
        
        return taskService.getTaskById(id)
                .map(existingTask -> {
                    // Check if the user is the owner of the task
                    if (!existingTask.getOwner().equals(currentUsername)) {
                        return new ResponseEntity<Void>(HttpStatus.FORBIDDEN);
                    }
                    
                    taskService.deleteTask(id);
                    return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}