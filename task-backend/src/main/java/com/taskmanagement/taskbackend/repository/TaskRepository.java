package com.taskmanagement.taskbackend.repository;

import com.taskmanagement.taskbackend.model.Task;
import com.taskmanagement.taskbackend.model.TaskStatus;
import com.taskmanagement.taskbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    List<Task> findByStatus(TaskStatus status);
    
    List<Task> findByOwner(User owner);
    
    List<Task> findByOwnerId(Long ownerId);
    
    List<Task> findByStatusAndOwner(TaskStatus status, User owner);
    
    List<Task> findByStatusAndOwnerId(TaskStatus status, Long ownerId);
}