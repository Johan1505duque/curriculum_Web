package com.hse.Curriculum.Dto.WorkExperienceDTO;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class WorkExperienceResponseDTO {

    private Integer workExperienceId;
    private Integer userId;
    private String companyName;
    private String jobTitle;
    private Integer countryId;
    private String countryName;
    private Integer departmentId;
    private String departmentName;
    private Integer municipalityId;
    private String municipalityName;
    private String functions;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean currentlyWorking;

}
