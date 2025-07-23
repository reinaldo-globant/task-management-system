package com.taskmanagement.taskbackend.service;

import com.taskmanagement.taskbackend.model.Task;
import com.taskmanagement.taskbackend.model.TaskStatus;
import com.taskmanagement.taskbackend.model.User;
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

    @Autowired
    public TaskService(TaskRepository taskRepository, StatusChangeRepository statusChangeRepository) {
        this.taskRepository = taskRepository;
        this.statusChangeRepository = statusChangeRepository;
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

    public List<Task> getTasksByOwnerId(Long ownerId) {
        return taskRepository.findByOwnerId(ownerId);
    }

    public List<Task> getTasksByStatusAndOwnerId(TaskStatus status, Long ownerId) {
        return taskRepository.findByStatusAndOwnerId(status, ownerId);
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