package com.hse.Curriculum.Dto.ProfileDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Schema(description = "Información completa del usuario (datos básicos + perfil)")
public class UserProfileDTO {

    // ========== DATOS DE USERS ==========
    @Schema(description = "ID del usuario")
    private Integer userId;

    @Schema(description = "Nombre del usuario", example = "Juan")
    private String firstName;

    @Schema(description = "Apellido del usuario", example = "Pérez")
    private String lastName;

    @Schema(description = "Correo electrónico", example = "juan@example.com")
    private String email;

    // ========== DATOS DE PROFILES ==========

    @Schema(description = "Tipo de documento", example = "Cédula de Ciudadanía")
    private String documentType;

    @Schema(description = "Número de documento", example = "1234567890")
    private String documentNumber;

    @Schema(description = "Número de teléfono", example = "3001234567")
    private String phoneNumber;

    @Schema(description = "Dirección residencial")
    private String residentialAddress;

    @Schema(description = "Fecha de nacimiento", example = "1990-01-15")
    private LocalDate birthDate;


    // ========== CONSTRUCTORES ==========
    public UserProfileDTO() {}

    // ========== GETTERS Y SETTERS ==========

    // Users
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    // Profiles

    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }

    public String getDocumentNumber() { return documentNumber; }
    public void setDocumentNumber(String documentNumber) { this.documentNumber = documentNumber; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public String getResidentialAddress() { return residentialAddress; }
    public void setResidentialAddress(String residentialAddress) { this.residentialAddress = residentialAddress; }
}
