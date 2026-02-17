package com.hse.Curriculum.Dto.UserDTO.Admin;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO para actualizar usuarios desde el panel de administración
 * Todos los campos son opcionales para permitir actualizaciones parciales
 * Los datos se distribuyen entre Users y Profiles según corresponda
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para actualizar un usuario (solo Admin)")
public class UserUpdateByAdminDTO {

    // ========== DATOS BÁSICOS (se actualizan en USERS) ==========

    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Schema(description = "Nombre del usuario", example = "Juan Carlos")
    private String firstName;

    @Size(min = 2, max = 100, message = "El apellido debe tener entre 2 y 100 caracteres")
    @Schema(description = "Apellido del usuario", example = "Pérez García")
    private String lastName;

    @Email(message = "Debe proporcionar un email válido")
    @Size(max = 150, message = "El email no puede exceder 150 caracteres")
    @Schema(description = "Correo electrónico único", example = "juan.perez@empresa.com")
    private String email;

    @Size(min = 8, max = 100, message = "La contraseña debe tener entre 8 y 100 caracteres")
    @Schema(description = "Nueva contraseña (opcional)", example = "NewPassword123!")
    private String password;

    // ========== DATOS DE IDENTIFICACIÓN (se actualizan en PROFILES) ==========

    @Schema(
            description = "Tipo de documento de identidad",
            example = "Cédula de Ciudadanía",
            allowableValues = {"Cédula de Ciudadanía", "Cédula de Extranjería", "Pasaporte", "Tarjeta de Identidad"}
    )
    private String documentType;

    @Pattern(
            regexp = "^[0-9]{6,15}$",
            message = "El número de documento debe contener entre 6 y 15 dígitos"
    )
    @Schema(description = "Número de documento", example = "1111111111")
    private String documentNumber;

    // ========== DATOS DE CONTACTO (se actualizan en PROFILES) ==========

    @Pattern(
            regexp = "^[0-9]{10}$",
            message = "El número de teléfono debe contener exactamente 10 dígitos"
    )
    @Schema(description = "Número de teléfono", example = "3222222222")
    private String phoneNumber;

    @Past(message = "La fecha de nacimiento debe ser anterior a la fecha actual")
    @Schema(description = "Fecha de nacimiento", example = "1999-10-12")
    private LocalDate birthDate;

    @NotBlank(message = "La Dirección residencial  es obligatorio")
    @Schema(description = "Dirección residencial", example = "Cra 1 Nª 23 - 56")
    private String residentialAddress;

    // ========== ASIGNACIONES ORGANIZACIONALES (se actualizan en USERS) ==========

    @Schema(description = "ID del nuevo rol", example = "2")
    private Integer roleId;

    @Schema(
            description = "ID del nuevo cargo (0 para remover cargo, null para no modificar)",
            example = "5"
    )
    private Integer chargeId;

    @Schema(description = "Estado del usuario", example = "true")
    private Boolean status;

    // ========== MÉTODOS AUXILIARES ==========

    /**
     * Verifica si hay algún campo de USERS para actualizar
     */
    public boolean hasUserFieldsToUpdate() {
        return firstName != null ||
                lastName != null ||
                email != null ||
                password != null ||
                roleId != null ||
                chargeId != null ||
                status != null;
    }

    /**
     * Verifica si hay algún campo de PROFILES para actualizar
     */
    public boolean hasProfileFieldsToUpdate() {
        return documentType != null ||
                documentNumber != null ||
                phoneNumber != null ||
                residentialAddress != null ||
                birthDate != null;
    }

    /**
     * Verifica si hay algún campo para actualizar
     */
    public boolean hasAnyFieldToUpdate() {
        return hasUserFieldsToUpdate() || hasProfileFieldsToUpdate();
    }

    @Override
    public String toString() {
        return "UserUpdateByAdminDTO{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", documentType='" + documentType + '\'' +
                ", documentNumber='" + documentNumber + '\'' +
                ", residentialAddress ="+ residentialAddress + '\''+
                ", phoneNumber='" + phoneNumber + '\'' +
                ", birthDate=" + birthDate +
                ", roleId=" + roleId +
                ", chargeId=" + chargeId +
                ", status=" + status +
                '}';
    }
}