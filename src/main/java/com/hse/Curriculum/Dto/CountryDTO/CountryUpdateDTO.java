package com.hse.Curriculum.Dto.CountryDTO;


import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CountryUpdateDTO {

    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String countryName;

    @Pattern(regexp = "^[A-Z]{2}$", message = "El código ISO-2 debe tener 2 letras mayúsculas")
    private String isoCode2;

    @Pattern(regexp = "^[A-Z]{3}$", message = "El código ISO-3 debe tener 3 letras mayúsculas")
    private String isoCode3;

    @Size(max = 10, message = "El código telefónico no puede exceder 10 caracteres")
    private String phoneCode;
}
