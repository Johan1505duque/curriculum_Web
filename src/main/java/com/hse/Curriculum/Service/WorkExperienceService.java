package com.hse.Curriculum.Service;

import com.hse.Curriculum.Dto.WorkExperienceDTO.*;
import com.hse.Curriculum.Models.WorkExperience;

import java.util.List;

public interface WorkExperienceService  {
    WorkExperienceResponseDTO createWorkExperience(Integer userId, WorkExperienceCreateDTO dto);
    List<WorkExperienceResponseDTO> getWorkExperiencesByUserId(Integer userId);
    WorkExperienceResponseDTO getWorkExperienceById(Integer workExperienceId, Integer userId, boolean isAdmin);
    WorkExperienceResponseDTO updateWorkExperience(Integer workExperienceId, Integer userId, WorkExperienceUpdateDTO dto);
    void deleteWorkExperience(Integer workExperienceId, Integer userId, boolean isAdmin);
    WorkExperience getWorkExperienceEntityById(Integer workExperienceId);
}
