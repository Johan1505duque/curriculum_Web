package com.hse.Curriculum.Dto.LoginDTO;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO para respuesta de autenticación
 */
@Schema(description = "Respuesta de autenticación exitosa")
public class AuthResponseDTO {

    @Schema(description = "ID del usuario", example = "1")
    private Integer userId;

    @Schema(description = "Email del usuario", example = "juan@email.com")
    private String email;

    @Schema(description = "Nombre completo", example = "Juan Pérez")
    private String fullName;

    @Schema(description = "Mensaje de éxito", example = "Autenticación exitosa")
    private String message;

    public AuthResponseDTO() {}

    public AuthResponseDTO(Integer userId, String email, String fullName, String message) {
        this.userId = userId;
        this.email = email;
        this.fullName = fullName;
        this.message = message;
    }

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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
