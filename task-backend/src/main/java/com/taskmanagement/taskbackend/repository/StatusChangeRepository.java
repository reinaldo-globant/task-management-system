package com.taskmanagement.taskbackend.repository;

import com.taskmanagement.taskbackend.model.StatusChange;
import com.taskmanagement.taskbackend.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatusChangeRepository extends JpaRepository<StatusChange, Long> {
    
    List<StatusChange> findByTaskOrderByChangeDateDesc(Task task);
}