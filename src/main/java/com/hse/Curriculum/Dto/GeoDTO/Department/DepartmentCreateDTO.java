package com.hse.Curriculum.Dto.GeoDTO.Department;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DepartmentCreateDTO {
    @NotNull(message = "El país es obligatorio")
    private Integer countryId;

    @NotBlank(message = "El nombre del departamento es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    private String name;

    @Size(max = 10, message = "El código DANE no puede superar 10 caracteres")
    private String daneCode;
}
