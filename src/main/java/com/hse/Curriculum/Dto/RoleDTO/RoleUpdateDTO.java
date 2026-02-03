package com.hse.Curriculum.Dto.RoleDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * DTO para actualizar un rol existente
 * Todos los campos son opcionales para permitir actualizaciones parciales
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para actualizar un rol (todos los campos son opcionales)")
public class RoleUpdateDTO {
    /**
     * Nombre del rol (único en el sistema)
     */
    @Size(min = 3, max = 50, message = "El nombre del rol debe tener entre 3 y 50 caracteres")
    @Pattern(regexp = "^[A-Z_]+$", message = "El nombre del rol debe estar en mayúsculas y solo contener letras y guiones bajos")
    @Schema(
            description = "Nombre del rol en mayúsculas",
            example = "SUPERVISOR"
    )
    private String name;

    /**
     * Descripción del rol
     */
    @Size(max = 150, message = "La descripción no puede exceder 150 caracteres")
    @Schema(
            description = "Descripción detallada del rol",
            example = "Rol de supervisor con permisos de monitoreo"
    )
    private String description;

    /**
     * Estado del rol (activo/inactivo)
     */
    @Schema(
            description = "Estado del rol (true = activo, false = inactivo)",
            example = "true"
    )
    private Boolean status;
}
