package com.taskmanagement.taskbackend.controller;

import com.taskmanagement.taskbackend.model.Task;
import com.taskmanagement.taskbackend.model.TaskStatus;
import com.taskmanagement.taskbackend.model.User;
import com.taskmanagement.taskbackend.payload.request.TaskRequest;
import com.taskmanagement.taskbackend.security.services.UserDetailsImpl;
import com.taskmanagement.taskbackend.service.TaskService;
import com.taskmanagement.taskbackend.service.UserService;
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
    private final UserService userService;

    @Autowired
    public TaskController(TaskService taskService, UserService userService) {
        this.taskService = taskService;
        this.userService = userService;
    }
    
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userService.getUserById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
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

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<Task>> getTasksByOwner(@PathVariable Long ownerId) {
        List<Task> tasks = taskService.getTasksByOwnerId(ownerId);
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }
    
    @GetMapping("/my-tasks")
    public ResponseEntity<List<Task>> getMyTasks() {
        User currentUser = getCurrentUser();
        List<Task> tasks = taskService.getTasksByOwnerId(currentUser.getId());
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    @GetMapping("/status/{status}/owner/{ownerId}")
    public ResponseEntity<List<Task>> getTasksByStatusAndOwner(
            @PathVariable TaskStatus status,
            @PathVariable Long ownerId) {
        List<Task> tasks = taskService.getTasksByStatusAndOwnerId(status, ownerId);
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }
    
    @GetMapping("/my-tasks/status/{status}")
    public ResponseEntity<List<Task>> getMyTasksByStatus(@PathVariable TaskStatus status) {
        User currentUser = getCurrentUser();
        List<Task> tasks = taskService.getTasksByStatusAndOwnerId(status, currentUser.getId());
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
        User currentUser = getCurrentUser();
        Task task = new Task(
                taskRequest.getTitle(),
                taskRequest.getDescription(),
                taskRequest.getStatus(),
                currentUser
        );
        Task createdTask = taskService.createTask(task);
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
        
        User currentUser = getCurrentUser();
        
        return taskService.getTaskById(id)
                .map(existingTask -> {
                    // Check if the user is the owner of the task
                    if (!existingTask.getOwner().getId().equals(currentUser.getId())) {
                        return new ResponseEntity<Task>(HttpStatus.FORBIDDEN);
                    }
                    
                    existingTask.setTitle(taskRequest.getTitle());
                    existingTask.setDescription(taskRequest.getDescription());
                    existingTask.setStatus(taskRequest.getStatus(), currentUser);
                    
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
        
        User currentUser = getCurrentUser();
        
        return taskService.getTaskById(id)
                .map(existingTask -> {
                    // Check if the user is the owner of the task
                    if (!existingTask.getOwner().getId().equals(currentUser.getId())) {
                        return new ResponseEntity<Void>(HttpStatus.FORBIDDEN);
                    }
                    
                    taskService.deleteTask(id);
                    return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}