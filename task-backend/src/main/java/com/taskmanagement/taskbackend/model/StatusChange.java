package com.taskmanagement.taskbackend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "status_changes")
public class StatusChange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private Task task;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TaskStatus previousStatus;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TaskStatus newStatus;

    @NotNull
    private LocalDateTime changeDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by_user_id")
    private User changedBy;

    // Default constructor
    public StatusChange() {
    }

    // Constructor with fields
    public StatusChange(Task task, TaskStatus previousStatus, TaskStatus newStatus, User changedBy) {
        this.task = task;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.changeDate = LocalDateTime.now();
        this.changedBy = changedBy;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public TaskStatus getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(TaskStatus previousStatus) {
        this.previousStatus = previousStatus;
    }

    public TaskStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(TaskStatus newStatus) {
        this.newStatus = newStatus;
    }

    public LocalDateTime getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(LocalDateTime changeDate) {
        this.changeDate = changeDate;
    }

    public User getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(User changedBy) {
        this.changedBy = changedBy;
    }
}