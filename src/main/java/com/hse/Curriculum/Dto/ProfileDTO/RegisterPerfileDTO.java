package com.hse.Curriculum.Dto.ProfileDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
/**
 * DTO para REGISTRAR el perfil del usuario (datos personales adicionales)
 */
@Schema(description = "Datos para crear o actualizar el perfil del usuario")

public class RegisterPerfileDTO {
    @NotBlank(message = "El tipo de documento es obligatorio")
    @Schema(description = "Tipo de documento", example = "Cédula de Ciudadanía")
    private String documentType;

    @NotBlank(message = "El número de documento es obligatorio")
    @Schema(description = "Número de documento", example = "1234567890")
    private String documentNumber;

    @NotBlank(message = "El número de teléfono es obligatorio")
    @Pattern(regexp = "^[0-9]{10}$", message = "El teléfono debe tener 10 dígitos")
    @Schema(description = "Número de teléfono", example = "3001234567")
    private String phoneNumber;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser en el pasado")
    @Schema(description = "Fecha de nacimiento", example = "1990-01-15")
    private LocalDate birthDate;



    // Constructores
    public RegisterPerfileDTO() {}

    public RegisterPerfileDTO(String documentType, String documentNumber,
                      String phoneNumber, LocalDate birthDate) {
        this.documentType = documentType;
        this.documentNumber = documentNumber;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
    }

    // Getters y Setters
    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }

    public String getDocumentNumber() { return documentNumber; }
    public void setDocumentNumber(String documentNumber) { this.documentNumber = documentNumber; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

}

