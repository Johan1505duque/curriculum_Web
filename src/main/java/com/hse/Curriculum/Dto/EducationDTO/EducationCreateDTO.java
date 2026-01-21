package com.hse.Curriculum.Dto.EducationDTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hse.Curriculum.Enum.EducationTypeEnum;
import com.hse.Curriculum.Enum.GraduateStatusEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO para crear un registro de educación
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EducationCreateDTO {

    @NotNull(message = "El tipo de educación es obligatorio")
    private EducationTypeEnum typeEducation;

    @NotBlank(message = "El nivel académico es obligatorio")
    @Size(max = 100, message = "El nivel académico no puede exceder 100 caracteres")
    private String academicLevel;

    @NotBlank(message = "El nombre de la institución es obligatorio")
    @Size(max = 255, message = "El nombre de la institución no puede exceder 255 caracteres")
    private String institutionName;

    @Size(max = 255, message = "El título no puede exceder 255 caracteres")
    private String degreeTitle;

    private Integer countryCode;

    private GraduateStatusEnum graduateStatus;

    @Size(max = 100, message = "El último nivel completado no puede exceder 100 caracteres")
    private String lastLevelCompleted;

    @PastOrPresent(message = "La fecha del último nivel debe ser anterior o igual a la fecha actual")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate lastLevelDate;
}
