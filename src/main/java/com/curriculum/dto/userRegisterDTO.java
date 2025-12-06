
package com.curriculum.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO para el registro de usuarios
 */
@Schema(description = "Datos requeridos para registrar un nuevo usuario")
public class userRegisterDTO {

    @Schema(description = "Nombre del usuario", example = "Juan", required = true)
    private String firstName;

    @Schema(description = "Apellido del usuario", example = "Pérez", required = true)
    private String lastName;

    @Schema(description = "Correo electrónico del usuario", example = "juan.perez@email.com", required = true)
    private String email;

    @Schema(description = "Contraseña del usuario (mínimo 6 caracteres)", example = "password123", required = true)
    private String password;

    // Constructor vacío (IMPORTANTE para Jackson)
    public userRegisterDTO() {
    }

    // Constructor con parámetros
    public userRegisterDTO(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // toString para debug (ÚTIL)
    @Override
    public String toString() {
        return "UserRegisterDTO{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", password='[PROTECTED]'" +
                '}';
    }
}
