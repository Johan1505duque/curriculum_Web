package com.hse.Curriculum.Dto.WorkExperienceDTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class WorkExperienceUpdateDTO {

    @Size(max = 150, message = "El nombre de la empresa no puede superar 150 caracteres")
    private String companyName;

    @Size(max = 150, message = "El nombre del cargo no puede superar 150 caracteres")
    private String jobTitle;

    private Integer countryId;

    private Integer departmentId;

    private Integer municipalityId;

    @Size(max = 2000, message = "Las funciones no pueden superar 2000 caracteres")
    private String functions;

    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    private LocalDate endDate;

    private Boolean currentlyWorking;
}
