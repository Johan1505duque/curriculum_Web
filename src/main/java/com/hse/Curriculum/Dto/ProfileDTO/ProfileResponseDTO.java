package com.hse.Curriculum.Dto.ProfileDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "Respuesta con datos del perfil")
public class ProfileResponseDTO {

    @Schema(description = "ID del usuario creado")
    private Integer userId;

    @Schema(description = "Nombre del usuario")
    private String firstName;

    @Schema(description = "Apellido del usuario")
    private String lastName;

    @Schema(description = "Email")
    private String email;

    @Schema(description = "Password")
    private String password;

    @Schema(description = "perfil_id")
    private Integer profile_id;

    @Schema(description = "Tipo de documento")
    private String documentType;

    @Schema(description = "Número de documento")
    private String documentNumber;

    @Schema(description = "Dirección residencial")
    private String residentialAddress;

    @Schema(description = "Número de teléfono")
    private String phoneNumber;

    @Schema(description = "Fecha de nacimiento")
    private LocalDate birthDate;

    @Schema(description = "Mensaje", example = "Usuario registrado exitosamente")
    private String message;


    // Constructor vacío
    public ProfileResponseDTO() {}


    // Getters y Setters
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Integer getProfileId() { return profile_id; }
    public void setProfileId(Integer profile_id) { this.profile_id = profile_id; }

    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }

    public String getDocumentNumber() { return documentNumber; }
    public void setDocumentNumber(String documentNumber) { this.documentNumber = documentNumber; }

    public String getResidentialAddress() { return residentialAddress; }
    public void setResidentialAddress(String residentialAddress) { this.residentialAddress = residentialAddress; }


    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

}
