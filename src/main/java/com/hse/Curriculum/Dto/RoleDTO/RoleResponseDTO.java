package com.hse.Curriculum.Dto.RoleDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para roles
 * Contiene todos los datos del rol incluyendo metadatos
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Respuesta con los datos de un rol")
public class RoleResponseDTO {
    /**
     * ID único del rol
     */
    @Schema(description = "ID único del rol", example = "1")
    private Integer roleId;

    /**
     * Nombre del rol
     */
    @Schema(description = "Nombre del rol en mayúsculas", example = "ADMIN")
    private String name;

    /**
     * Descripción del rol
     */
    @Schema(description = "Descripción detallada del rol", example = "Administrador con todos los permisos")
    private String description;

    /**
     * Estado del rol
     */
    @Schema(description = "Estado del rol (true = activo, false = inactivo)", example = "true")
    private Boolean status;

    /**
     * Fecha de creación del rol
     */
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @Schema(description = "Fecha y hora de creación del rol", example = "15/01/2024 10:30:00")
    private LocalDateTime createdAt;

    /**
     * Número de usuarios asignados a este rol
     */
    @Schema(description = "Cantidad de usuarios que tienen este rol", example = "25")
    private Long userCount;
}
