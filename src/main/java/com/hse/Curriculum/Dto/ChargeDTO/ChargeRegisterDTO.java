package com.hse.Curriculum.Dto.ChargeDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para registrar/crear un nuevo cargo (Post)
 */
@Data
@NoArgsConstructor
@Schema(description = "DTO para crear un nuevo cargo")
public class ChargeRegisterDTO {

    @NotBlank(message = "El nombre del cargo es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    @Schema(description = "Nombre del cargo", example = "CARGO_1", required = true)
    private String nameCharge;

    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    @Schema(description = "Descripción del cargo", example = "Descripción del cargo 1")
    private String description;

    // Constructor personalizado
    public ChargeRegisterDTO(String namePost, String description) {
        this.nameCharge = namePost;
        this.description = description;
    }
}
