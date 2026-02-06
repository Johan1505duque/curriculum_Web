package com.hse.Curriculum.Dto.ChargeDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para actualizar un cargo (Post) existente
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para actualizar un cargo existente")

public class ChargeUpdateDTO {
    @NotBlank(message = "El nombre del cargo es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    @Schema(description = "Nombre del cargo", example = "CARGO_1", required = true)
    private String nameCharge;

    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    @Schema(description = "Descripción del cargo", example = "Descripción actualizada del cargo 1")
    private String description;
}
