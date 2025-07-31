package com.taskmanagement.taskbackend.service;

import com.taskmanagement.taskbackend.model.Task;
import com.taskmanagement.taskbackend.model.TaskStatus;
import com.taskmanagement.taskbackend.payload.request.TaskRequest;
import com.taskmanagement.taskbackend.repository.StatusChangeRepository;
import com.taskmanagement.taskbackend.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final StatusChangeRepository statusChangeRepository;
    private final InternalUserServiceClient internalUserServiceClient;

    @Autowired
    public TaskService(TaskRepository taskRepository, StatusChangeRepository statusChangeRepository, InternalUserServiceClient internalUserServiceClient) {
        this.taskRepository = taskRepository;
        this.statusChangeRepository = statusChangeRepository;
        this.internalUserServiceClient = internalUserServiceClient;
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    public List<Task> getTasksByStatus(TaskStatus status) {
        return taskRepository.findByStatus(status);
    }

    public List<Task> getTasksByOwnerUsername(String ownerUsername) {
        return taskRepository.findByOwner(ownerUsername);
    }

    public List<Task> getTasksByStatusAndOwnerUsername(TaskStatus status, String ownerUsername) {
        return taskRepository.findByStatusAndOwner(status, ownerUsername);
    }

    @Transactional
    public Task createTask(TaskRequest taskRequest, String currentUser) {
        // Lookup the user ID using internal service
        Long userId = internalUserServiceClient.getUserIdByUsername(currentUser).block();
        if (userId == null) {
            throw new RuntimeException("User not found: " + currentUser);
        }
        
        Task task = new Task(
                taskRequest.getTitle(),
                taskRequest.getDescription(),
                taskRequest.getStatus(),
                userId,
                currentUser
        );
        return taskRepository.save(task);
    }

    @Transactional
    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    @Transactional
    public Task saveTask(Task task) {
        return taskRepository.save(task);
    }

    @Transactional
    public boolean deleteTask(Long id) {
        return taskRepository.findById(id)
                .map(task -> {
                    taskRepository.delete(task);
                    return true;
                })
                .orElse(false);
    }
}