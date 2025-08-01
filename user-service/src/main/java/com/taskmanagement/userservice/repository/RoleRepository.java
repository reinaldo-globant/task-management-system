package com.taskmanagement.userservice.repository;

import com.taskmanagement.userservice.model.ERole;
import com.taskmanagement.userservice.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}