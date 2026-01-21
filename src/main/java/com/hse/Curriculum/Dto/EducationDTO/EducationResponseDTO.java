package com.hse.Curriculum.Dto.EducationDTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hse.Curriculum.Enum.EducationTypeEnum;
import com.hse.Curriculum.Enum.GraduateStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO de respuesta para educación
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class EducationResponseDTO {
    private Integer educationId;
    private Integer userId;
    private EducationTypeEnum typeEducation;
    private String academicLevel;
    private String institutionName;
    private String degreeTitle;
    private Integer countryCode;
    private String countryName;
    private GraduateStatusEnum graduateStatus;
    private String lastLevelCompleted;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate lastLevelDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}
