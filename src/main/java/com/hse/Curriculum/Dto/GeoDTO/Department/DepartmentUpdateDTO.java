package com.hse.Curriculum.Dto.GeoDTO.Department;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DepartmentUpdateDTO {
    private Integer countryId;

    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    private String name;

    @Size(max = 10, message = "El código DANE no puede superar 10 caracteres")
    private String daneCode;
}
