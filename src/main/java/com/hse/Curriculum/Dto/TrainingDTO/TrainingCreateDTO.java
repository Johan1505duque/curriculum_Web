package com.hse.Curriculum.Dto.TrainingDTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.hse.Curriculum.Converter.Training.FlexibleDateDeserializer;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO para crear un nuevo registro de capacitación
 * Contiene las validaciones necesarias para los datos de entrada
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Datos para crear un nuevo registro de capacitación")
public class TrainingCreateDTO {
    /**
     * Nombre del curso o capacitación
     */
    @NotBlank(message = "El nombre del curso es obligatorio")
    @Size(min = 3, max = 200, message = "El nombre del curso debe tener entre 3 y 200 caracteres")
    @Schema(description = "Nombre del curso o capacitación", example = "Spring Boot Avanzado", required = true)
    private String courseName;

    /**
     * Tipo de registro de capacitación
     */
    @NotBlank(message = "El tipo de capacitación es obligatorio")
    @Size(max = 100, message = "El tipo de capacitación no puede exceder 100 caracteres")
    @Schema(
            description = "Tipo de capacitación",
            example = "Curso",
            allowableValues = {"Curso", "Certificación", "Diplomado", "Taller", "Seminario", "Especialización", "Otro"},
            required = true
    )
    private String trainingType;

    /**
     * Nombre de la institución
     */
    @NotBlank(message = "La institución es obligatoria")
    @Size(min = 2, max = 200, message = "La institución debe tener entre 2 y 200 caracteres")
    @Schema(description = "Institución que otorga la capacitación", example = "Udemy", required = true)
    private String institution;

    /**
     * Indica si el curso fue completado
     */
    @NotNull(message = "Debe indicar si el curso está finalizado")
    @Schema(description = "Indica si el curso fue completado", example = "true", required = true)
    private Boolean isCompleted;

    /**
     * Valor de la duración
     */
    @Min(value = 1, message = "La duración debe ser al menos 1")
    @Max(value = 9999, message = "La duración no puede exceder 9999")
    @Schema(description = "Valor numérico de la duración", example = "40")
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
            example = "15/01/2024",
            type = "string",
            format = "date",
            implementation = String.class
    )
    private LocalDate completionDate;
}
