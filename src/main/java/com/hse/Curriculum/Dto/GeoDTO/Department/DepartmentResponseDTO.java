package com.hse.Curriculum.Dto.GeoDTO.Department;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DepartmentResponseDTO {
    private Integer departmentId;
    private Integer countryId;
    private String  countryName;
    private String  name;
    private String  daneCode;
}
