package com.hse.Curriculum.Dto.UserDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Datos para registro inicial de usuario")
public class UserSignUpDTO {
    @NotBlank(message = "El nombre es obligatorio")
    @Schema(description = "Nombre del usuario", example = "Juan", required = true)
    private String firstName;

    @NotBlank(message = "El apellido es obligatorio")
    @Schema(description = "Apellido del usuario", example = "Pérez", required = true)
    private String lastName;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Email inválido")
    @Schema(description = "Correo electrónico", example = "juan@example.com", required = true)
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    @Schema(description = "Contraseña", example = "password123", required = true)
    private String password;

    // Constructores, Getters y Setters
    public UserSignUpDTO() {}

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    @Override
    public String toString() {
        return "UserSignUpDTO{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
