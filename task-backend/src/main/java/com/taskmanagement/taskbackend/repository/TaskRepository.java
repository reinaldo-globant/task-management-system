package com.taskmanagement.taskbackend.repository;

import com.taskmanagement.taskbackend.model.Task;
import com.taskmanagement.taskbackend.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    List<Task> findByStatus(TaskStatus status);
    
    List<Task> findByOwner(String owner);
    
    List<Task> findByStatusAndOwner(TaskStatus status, String owner);
}