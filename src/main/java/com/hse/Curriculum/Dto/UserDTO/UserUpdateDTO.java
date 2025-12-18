package com.hse.Curriculum.Dto.UserDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

/**
 * DTO para ACTUALIZAR el perfil del usuario (datos personales adicionales)
 */
public class UserUpdateDTO {
    @Schema(description = "Tipo de documento", example = "Cédula de Ciudadanía")
    private String documentType;

    @Schema(description = "Número de documento", example = "1234567890")
    private String documentNumber;

    @Schema(description = "Número de teléfono", example = "3001234567")
    private String phoneNumber;

    @Schema(description = "Fecha de nacimiento", example = "1990-01-15")
    private LocalDate birthDate;

    // Constructor vacío
    public UserUpdateDTO() {}

    // Constructor con parámetros
    public UserUpdateDTO(String documentType, String documentNumber,
                         String phoneNumber, LocalDate birthDate) {
        this.documentType = documentType;
        this.documentNumber = documentNumber;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
    }

    // Getters y Setters
    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    @Override
    public String toString() {
        return "UpdateProfileDTO{" +
                "documentType='" + documentType + '\'' +
                ", documentNumber='" + documentNumber + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", birthDate=" + birthDate +
                '}';
    }
}
