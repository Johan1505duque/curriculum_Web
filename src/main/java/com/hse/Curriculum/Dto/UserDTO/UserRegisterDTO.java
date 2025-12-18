
package com.hse.Curriculum.Dto.UserDTO;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

/**
 * DTO para el registro de usuarios
 */
@Schema(description = "Datos requeridos para registrar un nuevo usuario")
public class UserRegisterDTO {

    @Schema(description = "Nombre del usuario", example = "Juan", required = true)
    private String firstName;

    @Schema(description = "Apellido del usuario", example = "Pérez", required = true)
    private String lastName;

    @Schema(description = "Cedula Ciudania", example = "Pérez", required = true)
    private String documentType;

    @Schema(description = "1111111111", example = "1111111111", required = true)
    private String documentNUmber;

    @Schema(description = "3222222222", example = "3222222222", required = true)
    private String phoneNumber;

    @Schema(description = "1999-10-12", example = "1999-10-12", required = true)
    private LocalDate birthDate;

    // Constructor vacío (IMPORTANTE para Jackson)
    public UserRegisterDTO() {
    }

    // Constructor con parámetros
    public UserRegisterDTO(String firstName,
                           String lastName,
                           String documentType,
                           String documentNUmber,
                           String phoneNumber,
                           LocalDate birthDate) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.documentType = documentType;
        this.documentNUmber = documentNUmber;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate ;
    }

    // Getters y Setters (IMPORTANTES)
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getdocumentType() {
        return documentType;
    }

    public void setdocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getdocumentNUmber() {
        return documentNUmber;
    }

    public void setdocumentNUmber(String documentNUmber) {
        this.documentNUmber = documentNUmber;
    }

    public String getphoneNumber() {
        return phoneNumber;
    }

    public void setphoneNumber(String phoneNumber) {this.phoneNumber = phoneNumber;
    }

    public LocalDate getbirthDate() {
        return birthDate;
    }

    public void setbirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }



    // toString para debug (ÚTIL)
    @Override
    public String toString() {
        return "UserRegisterDTO{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", documentType='" + documentType + '\'' +
                ", documentNUmber='" + documentNUmber + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", birthDate='" + birthDate + '\'' +
                '}';
    }
}
