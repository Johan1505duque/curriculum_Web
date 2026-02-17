
package com.hse.Curriculum.Dto.UserDTO.Admin;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

/**
 * DTO para respuesta detallada de un usuario
 * Combina datos de Users y Profiles
 * Incluye toda la información del usuario incluyendo relaciones
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Información detallada de un usuario")
public class UserDetailResponseDTO {

    // ========== IDENTIFICACIÓN (de USERS) ==========

    @Schema(description = "ID del usuario", example = "1")
    private Integer userId;

    @Schema(description = "Nombre completo", example = "Juan Pérez")
    private String fullName;

    @Schema(description = "Nombre", example = "Juan")
    private String firstName;

    @Schema(description = "Apellido", example = "Pérez")
    private String lastName;

    @Schema(description = "Email", example = "juan.perez@empresa.com")
    private String email;

    @Schema(description = "Estado del usuario", example = "true")
    private Boolean status;

    // ========== DATOS DE IDENTIFICACIÓN (de PROFILES) ==========

    @Schema(description = "Tipo de documento", example = "Cédula de Ciudadanía")
    private String documentType;

    @Schema(description = "Número de documento", example = "1111111111")
    private String documentNumber;

    // ========== DATOS DE CONTACTO (de PROFILES) ==========

    @Schema(description = "Número de teléfono", example = "3222222222")
    private String phoneNumber;

    @Schema(description = "Fecha de nacimiento", example = "1999-10-12")
    private LocalDate birthDate;

    @Schema(description = "Edad calculada", example = "25")
    private Integer age;

    @NotBlank(message = "La Dirección residencial  es obligatorio")
    @Schema(description = "Dirección residencial", example = "Cra 1 Nª 23 - 56")
    private String residentialAddress;


    // ========== INFORMACIÓN DEL ROL (de USERS) ==========

    @Schema(description = "ID del rol", example = "1")
    private Integer roleId;

    @Schema(description = "Nombre del rol", example = "ADMIN")
    private String roleName;

    // ========== INFORMACIÓN DEL CARGO (de USERS) ==========

    @Schema(description = "ID del cargo", example = "3")
    private Integer chargeId;

    @Schema(description = "Nombre del cargo", example = "Director de Tecnología")
    private String chargeName;

    @Schema(description = "Descripción del cargo")
    private String chargeDescription;

    // ========== AUDITORÍA (de USERS) ==========

    @Schema(description = "Fecha de creación", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Fecha de última actualización", example = "2024-02-06T14:20:00")
    private LocalDateTime updatedAt;

    /**
     * Constructor desde entidad Users
     * Extrae datos de Users y de su Profiles relacionado
     */
    public UserDetailResponseDTO(com.hse.Curriculum.Models.Users user) {
        // ========== Datos de USERS ==========
        this.userId = user.getUserId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.fullName = user.getFirstName() + " " + user.getLastName();
        this.email = user.getEmail();
        this.status = user.getStatus();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();

        // ========== Datos de PROFILES ==========
        if (user.getProfile() != null) {
            this.documentType = user.getProfile().getDocumentType();
            this.documentNumber = user.getProfile().getDocumentNumber();
            this.phoneNumber = user.getProfile().getPhoneNumber();
            this.birthDate = user.getProfile().getBirthDate();
            this.residentialAddress = user.getProfile().getResidentialAddress();

            // Calcular edad si existe fecha de nacimiento
            if (user.getProfile().getBirthDate() != null) {
                this.age = calculateAge(user.getProfile().getBirthDate());
            }
        }

        // ========== Datos de ROL ==========
        if (user.getRole() != null) {
            this.roleId = user.getRole().getRoleId();
            this.roleName = user.getRole().getName();
        }

        // ========== Datos de CARGO ==========
        if (user.getCharge() != null) {
            this.chargeId = user.getCharge().getChargeId();
            this.chargeName = user.getCharge().getNameCharge();
            this.chargeDescription = user.getCharge().getDescription();
        }
    }

    /**
     * Calcula la edad basándose en la fecha de nacimiento
     */
    private Integer calculateAge(LocalDate birthDate) {
        if (birthDate == null) {
            return null;
        }
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
}