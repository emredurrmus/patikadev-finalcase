package com.edurmus.librarymanagement.model.entity;

import com.edurmus.librarymanagement.model.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Role extends BaseEntity implements GrantedAuthority {

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role",nullable = false)
    private UserRole userRole;

    @Override
    public String getAuthority() {
        return userRole.name();
    }
}
