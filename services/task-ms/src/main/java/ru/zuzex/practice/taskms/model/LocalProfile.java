package ru.zuzex.practice.taskms.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "local_profile", schema = "task_dev")
@NoArgsConstructor
@AllArgsConstructor
public class LocalProfile {
    @Id
    @Column(name = "id", nullable = false)
    private UUID profileId;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
