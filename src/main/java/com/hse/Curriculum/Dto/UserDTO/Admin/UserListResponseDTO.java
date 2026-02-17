package com.hse.Curriculum.Dto.UserDTO.Admin;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.hse.Curriculum.Models.Users;
import com.hse.Curriculum.Models.Profiles;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO simplificado para listar usuarios
 * Usado en endpoints que retornan múltiples usuarios
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Información resumida de usuario para listados")
public class UserListResponseDTO {

    @Schema(description = "ID del usuario", example = "1")
    private Integer userId;

    @Schema(description = "Nombre completo", example = "Juan Pérez")
    private String fullName;

    @Schema(description = "Email", example = "juan.perez@empresa.com")
    private String email;

    @Schema(description = "Número de documento", example = "1111111111")
    private String documentType;

    @Schema(description = "Número de documento", example = "1111111111")
    private String documentNumber;

    @Schema(description = "Número de teléfono", example = "3222222222")
    private String phoneNumber;

    @NotBlank(message = "La Dirección residencial  es obligatorio")
    @Schema(description = "Dirección residencial", example = "Cra 1 Nª 23 - 56")
    private String residentialAddress;


    @Schema(description = "Estado", example = "true")
    private Boolean status;

    @Schema(description = "Nombre del rol", example = "ADMIN")
    private String roleName;

    @Schema(description = "Nombre del cargo", example = "Director de Tecnología")
    private String chargeName;

    @Schema(description = "Fecha de creación", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;

    /**
     * Constructor desde entidad Users
     */
    public UserListResponseDTO(Users user) {
        this.userId = user.getUserId();
        this.fullName = user.getFirstName() + " " + user.getLastName();
        this.email = user.getEmail();
        this.status = user.getStatus();
        this.createdAt = user.getCreatedAt();

        // 🔥 Datos del PERFIL (documentNumber, phoneNumber)
        if (user.getProfile() != null) {
            this.documentType = user.getProfile().getDocumentType();
            this.documentNumber = user.getProfile().getDocumentNumber();
            this.phoneNumber = user.getProfile().getPhoneNumber();
            this.residentialAddress = user.getProfile().getResidentialAddress();
        }

        if (user.getRole() != null) {
            this.roleName = user.getRole().getName();
        }

        if (user.getCharge() != null) {
            this.chargeName = user.getCharge().getNameCharge();
        }
    }
}