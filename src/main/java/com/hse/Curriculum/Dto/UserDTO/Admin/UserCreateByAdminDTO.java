package com.hse.Curriculum.Dto.UserDTO.Admin;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO para crear usuarios desde el panel de administración
 * Los datos personales (documento, teléfono, fecha nacimiento) se guardarán en Profiles
 * Los datos de autenticación y rol se guardan en Users
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para crear un usuario (solo Admin)")
public class UserCreateByAdminDTO {

    // ========== DATOS BÁSICOS (se guardan en USERS) ==========

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Schema(description = "Nombre del usuario", example = "Juan", required = true)
    private String firstName;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 2, max = 100, message = "El apellido debe tener entre 2 y 100 caracteres")
    @Schema(description = "Apellido del usuario", example = "Pérez", required = true)
    private String lastName;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Debe proporcionar un email válido")
    @Size(max = 150, message = "El email no puede exceder 150 caracteres")
    @Schema(description = "Correo electrónico único", example = "juan.perez@empresa.com", required = true)
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, max = 100, message = "La contraseña debe tener entre 8 y 100 caracteres")
    @Schema(description = "Contraseña del usuario", example = "Password123!", required = true)
    private String password;

    // ========== DATOS DE IDENTIFICACIÓN (se guardan en PROFILES) ==========

    @NotBlank(message = "El tipo de documento es obligatorio")
    @Schema(
            description = "Tipo de documento de identidad",
            example = "Cédula de Ciudadanía",
            allowableValues = {"Cédula de Ciudadanía", "Cédula de Extranjería", "Pasaporte", "Tarjeta de Identidad"},
            required = true
    )
    private String documentType;

    @NotBlank(message = "El número de documento es obligatorio")
    @Pattern(
            regexp = "^[0-9]{6,15}$",
            message = "El número de documento debe contener entre 6 y 15 dígitos"
    )
    @Schema(description = "Número de documento", example = "1111111111", required = true)
    private String documentNumber;

    // ========== DATOS DE CONTACTO (se guardan en PROFILES) ==========

    @NotBlank(message = "El número de teléfono es obligatorio")
    @Pattern(
            regexp = "^[0-9]{10}$",
            message = "El número de teléfono debe contener exactamente 10 dígitos"
    )
    @Schema(description = "Número de teléfono", example = "3222222222", required = true)
    private String phoneNumber;

    @NotBlank(message = "La Dirección residencial  es obligatorio")
    @Schema(description = "Dirección residencial", example = "Cra 1 Nª 23 - 56")
    private String residentialAddress;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser anterior a la fecha actual")
    @Schema(description = "Fecha de nacimiento", example = "1999-10-12", required = true)
    private LocalDate birthDate;

    // ========== ASIGNACIONES ORGANIZACIONALES (se guardan en USERS) ==========

    @NotNull(message = "El rol es obligatorio")
    @Schema(
            description = "ID del rol a asignar",
            example = "1",
            allowableValues = {"1", "2"},
            required = true
    )
    private Integer roleId;

    @Schema(
            description = "ID del cargo a asignar (opcional)",
            example = "3"
    )
    private Integer chargeId;

    // ========== MÉTODOS AUXILIARES ==========

    /**
     * Calcula la edad del usuario basándose en la fecha de nacimiento
     */
    public Integer getAge() {
        if (birthDate == null) {
            return null;
        }
        return LocalDate.now().getYear() - birthDate.getYear();
    }

    /**
     * Retorna el nombre completo del usuario
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public String toString() {
        return "UserCreateByAdminDTO{" +
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
                '}';
    }
}