package com.hse.Curriculum.Dto.UserDTO;

import io.swagger.v3.oas.annotations.media.Schema;

public class UserResponseDTO {

    @Schema(description = "ID del usuario", example = "1")
    private Integer userId;

    @Schema(description = "Email del usuario", example = "juan.perez@email.com")
    private String email;

    @Schema(description = "Mensaje", example = "Usuario registrado exitosamente")
    private String message;

    // Constructor vacío
    public UserResponseDTO() {
    }

    // Constructor con parámetros
    public UserResponseDTO(Integer userId, String email, String message) {
        this.userId = userId;
        this.email = email;
        this.message = message;
    }

    // Getters y Setters
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
