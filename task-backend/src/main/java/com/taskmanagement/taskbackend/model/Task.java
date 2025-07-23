package com.taskmanagement.taskbackend.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String title;
    
    private String description;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    private TaskStatus status;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    
    @NotNull
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StatusChange> statusChanges = new ArrayList<>();

    // Default constructor
    public Task() {
        this.createdAt = LocalDateTime.now();
    }

    // Constructor with fields
    public Task(String title, String description, TaskStatus status, User owner) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.owner = owner;
        this.createdAt = LocalDateTime.now();
    }
    
    // Method to add status change
    public void addStatusChange(TaskStatus previousStatus, TaskStatus newStatus, User changedBy) {
        StatusChange statusChange = new StatusChange(this, previousStatus, newStatus, changedBy);
        this.statusChanges.add(statusChange);
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status, User changedBy) {
        if (this.status != null && !this.status.equals(status)) {
            addStatusChange(this.status, status, changedBy);
        }
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public List<StatusChange> getStatusChanges() {
        return statusChanges;
    }

    public void setStatusChanges(List<StatusChange> statusChanges) {
        this.statusChanges = statusChanges;
    }
}