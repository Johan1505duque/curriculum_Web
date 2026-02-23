package com.hse.Curriculum.Dto.GeoDTO.Municipality;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MunicipalityCreateDTO {
    @NotNull(message = "El departamento es obligatorio")
    private Integer departmentId;

    @NotBlank(message = "El nombre del municipio es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    private String name;

    @Size(max = 10, message = "El código DANE no puede superar 10 caracteres")
    private String daneCode;

    private Boolean isCapital = false;

}
