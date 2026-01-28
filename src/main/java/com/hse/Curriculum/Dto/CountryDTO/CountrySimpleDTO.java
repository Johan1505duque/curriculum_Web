package com.hse.Curriculum.Dto.CountryDTO;

import lombok.*;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
 public class CountrySimpleDTO {
    private Integer countryId;
    private Integer countryCode;
    private String countryName;
    private String isoCode2;
}
