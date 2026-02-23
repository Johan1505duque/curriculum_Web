package com.hse.Curriculum.Dto.GeoDTO.Municipality;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MunicipalityResponseDTO {
    private Integer municipalityId;
    private Integer departmentId;
    private String  departmentName;
    private Integer countryId;
    private String  countryName;
    private String  name;
    private String  daneCode;
    private Boolean isCapital;
}
