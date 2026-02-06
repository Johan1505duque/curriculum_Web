package com.hse.Curriculum.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entidad que representa los cargos de un usuario
 */
@Entity
@Table(name = "charge")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Charge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "charge_id")
    private Integer chargeId;

    @Column(name = "name_charge", nullable = false, unique = true, length = 100)
    private String nameCharge;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "status", nullable = false)
    private Boolean status = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "created_by")
    private Integer createdBy;

    @Column(name = "updated_by")
    private Integer updatedBy;
}
