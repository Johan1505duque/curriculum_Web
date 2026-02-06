package com.hse.Curriculum.Dto.PostDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para cargos (Post)
 * Incluye todos los campos incluyendo los de auditoría
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO de respuesta con información completa del cargo")
public class PostResponseDTO {
    @Schema(description = "ID del cargo", example = "1")
    private Integer postId;

    @Schema(description = "Nombre del cargo", example = "CARGO_1")
    private String namePost;

    @Schema(description = "Descripción del cargo", example = "Descripción del cargo 1")
    private String description;

    @Schema(description = "Estado del cargo (activo/inactivo)", example = "true")
    private Boolean status;

    @Schema(description = "Fecha de creación", example = "2026-02-05 16:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Fecha de última actualización", example = "2026-02-05 16:30:00")
    private LocalDateTime updatedAt;

    @Schema(description = "ID del usuario que creó el cargo", example = "1")
    private Integer createdBy;

    @Schema(description = "ID del usuario que actualizó el cargo", example = "1")
    private Integer updatedBy;
}
