package com.hse.Curriculum.Dto.ProfileDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

/**
 * DTO para ACTUALIZAR datos del usuario y perfil
 */
@Schema(description = "Datos para actualizar usuario y perfil  ")
public class ProfileUpdateDTO {
    // ========== DATOS DE USERS ==========
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100)
    @Schema(description = "Nombre del usuario", example = "Juan")
    private String firstName;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 2, max = 100)
    @Schema(description = "Apellido del usuario", example = "Pérez")
    private String lastName;

    // ========== DATOS DE PROFILES ==========
    @NotBlank(message = "El tipo de documento es obligatorio")
    @Schema(description = "Tipo de documento", example = "Cédula de Ciudadanía")
    private String documentType;

    @NotBlank(message = "El número de documento es obligatorio")
    @Schema(description = "Número de documento", example = "1234567890")
    private String documentNumber;

    @NotBlank(message = "La Dirección residencial  es obligatorio")
    @Schema(description = "Dirección residencial", example = "Cra 1 Nª 23 - 56")
    private String residentialAddress;

    @NotBlank(message = "El número de teléfono es obligatorio")
    @Pattern(regexp = "^[0-9]{10}$", message = "El teléfono debe tener 10 dígitos")
    @Schema(description = "Número de teléfono", example = "3001234567")
    private String phoneNumber;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser en el pasado")
    @Schema(description = "Fecha de nacimiento", example = "1990-01-15")
    private LocalDate birthDate;

    // ========== CONSTRUCTORES ==========
    public ProfileUpdateDTO() {}

    // ========== GETTERS Y SETTERS ==========
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }

    public String getDocumentNumber() { return documentNumber; }
    public void setDocumentNumber(String documentNumber) { this.documentNumber = documentNumber; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getResidentialAddress() { return residentialAddress; }
    public void setResidentialAddress(String residentialAddress) { this.residentialAddress = residentialAddress; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
}
