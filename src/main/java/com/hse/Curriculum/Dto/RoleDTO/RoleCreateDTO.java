package com.hse.Curriculum.Dto.RoleDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para crear un nuevo rol en el sistema
 * Contiene las validaciones necesarias para los datos de entrada
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Datos para crear un nuevo rol")
public class RoleCreateDTO {

    /**
     * Nombre del rol (único en el sistema)
     */
    @NotBlank(message = "El nombre del rol es obligatorio")
    @Size(min = 3, max = 50, message = "El nombre del rol debe tener entre 3 y 50 caracteres")
    @Pattern(regexp = "^[A-Z_]+$", message = "El nombre del rol debe estar en mayúsculas y solo contener letras y guiones bajos")
    @Schema(
            description = "Nombre del rol en mayúsculas (ej: ADMIN, USER, SUPPORT)",
            example = "MANAGER",
            required = true
    )
    private String name;

    /**
     * Descripción del rol
     */
    @Size(max = 150, message = "La descripción no puede exceder 150 caracteres")
    @Schema(
            description = "Descripción detallada del rol y sus permisos",
            example = "Rol de gerente con permisos de gestión de equipos"
    )
    private String description;

    /**
     * Estado del rol (activo/inactivo)
     */
    @Schema(
            description = "Estado del rol (true = activo, false = inactivo)",
            example = "true",
            defaultValue = "true"
    )
    private Boolean status;
}
