package com.hse.Curriculum.Dto.TrainingDTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import com.hse.Curriculum.Converter.Training.FlexibleDateDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO para actualizar un registro de capacitación existente
 * Todos los campos son opcionales para permitir actualizaciones parciales
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Datos para actualizar un registro de capacitación (todos los campos son opcionales)")
public class TrainingUpdateDTO {
    /**
     * Nombre del curso o capacitación
     */
    @Size(min = 3, max = 200, message = "El nombre del curso debe tener entre 3 y 200 caracteres")
    @Schema(description = "Nombre del curso o capacitación", example = "Spring Boot Avanzado - Actualizado")
    private String courseName;

    /**
     * Tipo de registro de capacitación
     */
    @Size(max = 100, message = "El tipo de capacitación no puede exceder 100 caracteres")
    @Schema(
            description = "Tipo de capacitación",
            example = "Certificación",
            allowableValues = {"Curso", "Certificación", "Diplomado", "Taller", "Seminario", "Especialización", "Otro"}
    )
    private String trainingType;

    /**
     * Nombre de la institución
     */
    @Size(min = 2, max = 200, message = "La institución debe tener entre 2 y 200 caracteres")
    @Schema(description = "Institución que otorga la capacitación", example = "Coursera")
    private String institution;

    /**
     * Indica si el curso fue completado
     */
    @Schema(description = "Indica si el curso fue completado", example = "true")
    private Boolean isCompleted;

    /**
     * Valor de la duración
     */
    @Min(value = 1, message = "La duración debe ser al menos 1")
    @Max(value = 9999, message = "La duración no puede exceder 9999")
    @Schema(description = "Valor numérico de la duración", example = "50")
    private Integer durationValue;

    /**
     * Unidad de tiempo para la duración
     */
    @Size(max = 50, message = "La unidad de duración no puede exceder 50 caracteres")
    @Schema(
            description = "Unidad de tiempo para la duración",
            example = "Horas",
            allowableValues = {"Horas", "Días", "Semanas", "Meses", "Años"}
    )
    private String durationUnit;

    /**
     * Fecha de finalización del curso
     * Acepta múltiples formatos: dd/MM/yyyy, yyyy-MM-dd, dd-MM-yyyy, etc.
     */
    @PastOrPresent(message = "La fecha de finalización no puede ser futura")
    @JsonDeserialize(using = FlexibleDateDeserializer.class)
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Schema(
            description = "Fecha de finalización del curso (acepta múltiples formatos)",
            example = "20/01/2024",
            type = "string",
            format = "date",
            implementation = String.class
    )
    private LocalDate completionDate;
}
