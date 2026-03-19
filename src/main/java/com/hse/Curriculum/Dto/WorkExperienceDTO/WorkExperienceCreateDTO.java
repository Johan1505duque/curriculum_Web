package com.hse.Curriculum.Dto.WorkExperienceDTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class WorkExperienceCreateDTO {

    @NotBlank(message = "El nombre de la empresa es obligatorio")
    @Size(max = 150, message = "El nombre de la empresa no puede superar 150 caracteres")
    private String companyName;

    @NotBlank(message = "El nombre del cargo es obligatorio")
    @Size(max = 150, message = "El nombre del cargo no puede superar 150 caracteres")
    private String jobTitle;

    @NotNull(message = "El paí s es obligatorio")
    private Integer countryId;

    private Integer departmentId;

    private Integer municipalityId;

    @Size(max = 2000, message = "Las funciones no pueden superar 2000 caracteres")
    private String functions;

    @NotNull(message = "La fecha de inicio es obligatoria")
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    private LocalDate endDate;

    @NotNull(message = "Debe indicar si actualmente está laborando")
    private Boolean currentlyWorking = false;
}
