package com.hse.Curriculum.Dto.TrainingDTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO de respuesta para registros de capacitación
 * Contiene todos los datos del registro incluyendo información del usuario
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Respuesta con los datos de un registro de capacitación")
public class TrainingResponseDTO {
    /**
     * ID único del registro de capacitación
     */
    @Schema(description = "ID único del registro", example = "1")
    private Integer trainingId;

    /**
     * ID del usuario propietario
     */
    @Schema(description = "ID del usuario propietario del registro", example = "5")
    private Integer userId;

    /**
     * Nombre completo del usuario
     */
    @Schema(description = "Nombre completo del usuario", example = "Juan Pérez")
    private String userName;

    /**
     * Email del usuario
     */
    @Schema(description = "Email del usuario", example = "juan.perez@example.com")
    private String userEmail;

    /**
     * Nombre del curso o capacitación
     */
    @Schema(description = "Nombre del curso o capacitación", example = "Spring Boot Avanzado")
    private String courseName;

    /**
     * Tipo de registro de capacitación
     */
    @Schema(description = "Tipo de capacitación", example = "Curso")
    private String trainingType;

    /**
     * Nombre de la institución
     */
    @Schema(description = "Institución que otorga la capacitación", example = "Udemy")
    private String institution;

    /**
     * Indica si el curso fue completado
     */
    @Schema(description = "Indica si el curso fue completado", example = "true")
    private Boolean isCompleted;

    /**
     * Valor de la duración
     */
    @Schema(description = "Valor numérico de la duración", example = "40")
    private Integer durationValue;

    /**
     * Unidad de tiempo para la duración
     */
    @Schema(description = "Unidad de tiempo para la duración", example = "Horas")
    private String durationUnit;

    /**
     * Duración formateada en texto
     */
    @Schema(description = "Duración formateada completa", example = "40 Horas")
    private String durationFormatted;

    /**
     * Fecha de finalización del curso
     */
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Schema(description = "Fecha de finalización del curso", example = "15/01/2024")
    private LocalDate completionDate;

    /**
     * Fecha de creación del registro
     */
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @Schema(description = "Fecha y hora de creación del registro", example = "10/01/2024 14:30:00")
    private LocalDateTime createdAt;

    /**
     * Fecha de última actualización
     */
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @Schema(description = "Fecha y hora de última actualización", example = "15/01/2024 16:45:00")
    private LocalDateTime updatedAt;
}
