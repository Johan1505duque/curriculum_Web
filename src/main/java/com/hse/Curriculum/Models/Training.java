package com.hse.Curriculum.Models;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad que representa un registro de capacitación o curso de un usuario
 * Mapea la tabla 'trainings' en la base de datos
 */
@Entity
@Table(name = "trainings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Training {
    /**
     * Identificador único del registro de capacitación
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "training_id")
    private Integer trainingId;

    /**
     * Usuario al que pertenece este registro de capacitación
     * Relación Many-to-One con Users
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    /**
     * Nombre del curso o capacitación
     */
    @Column(name = "course_name", nullable = false, length = 200)
    private String courseName;

    /**
     * Tipo de registro (Curso, Certificación, Diplomado, Taller, etc.)
     */
    @Column(name = "training_type", nullable = false, length = 100)
    private String trainingType;

    /**
     * Nombre de la institución que otorga la capacitación
     */
    @Column(name = "institution", nullable = false, length = 200)
    private String institution;

    /**
     * Indica si el curso/capacitación fue finalizado
     */
    @Column(name = "is_completed", nullable = false)
    private Boolean isCompleted;

    /**
     * Duración del curso en la unidad especificada
     */
    @Column(name = "duration_value")
    private Integer durationValue;

    /**
     * Unidad de tiempo para la duración (Horas, Días, Semanas, Meses, Años)
     */
    @Column(name = "duration_unit", length = 50)
    private String durationUnit;

    /**
     * Fecha de finalización del curso (puede ser nula si no está completado)
     */
    @Column(name = "completion_date")
    private LocalDate completionDate;

    /**
     * Fecha de creación del registro (automática)
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Fecha de última actualización del registro (automática)
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
