package com.edurmus.librarymanagement.repository;

import com.edurmus.librarymanagement.model.entity.Role;
import com.edurmus.librarymanagement.model.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface RoleRepository extends JpaRepository<Role, Long> {
    
    Set<Role> findByUserRole(UserRole userRole);

}
