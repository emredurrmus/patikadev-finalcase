package com.edurmus.librarymanagement.model.entity;

import com.edurmus.librarymanagement.util.SecurityUtils;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean active = true;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String createdBy;

    private String updatedBy;

    @PrePersist
    protected void onCreate() {
        updateAuditFields();
    }

    @PreUpdate
    protected void onUpdate() {
        updateAuditFields();
    }

    private void updateAuditFields() {
        LocalDateTime currentTime = LocalDateTime.now();
        String currentUsername = SecurityUtils.getCurrentUserName();

        if (this.createdAt == null) {
            this.createdAt = currentTime;
            this.createdBy = currentUsername;
        }
        this.updatedAt = currentTime;
        this.updatedBy = currentUsername;
    }


}
