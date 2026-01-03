package com.hse.Curriculum.Dto.ProfileDTO;

import io.swagger.v3.oas.annotations.media.Schema;

public class ProfessionalProfileResponseDTO {

    // Campos opcionales del complemento
    @Schema(description = "Resumen profesional")
    private String professionalSummary;

    @Schema(description = "Logros profesionales")
    private String careerAchievements;

    public   ProfessionalProfileResponseDTO() {}

    public ProfessionalProfileResponseDTO(String professionalSummary, String careerAchievements){

        this.professionalSummary = professionalSummary;
        this.careerAchievements = careerAchievements;
    }

    // Getters y Setters
    public String getProfessionalSummary() { return professionalSummary; }
    public void setProfessionalSummary(String professionalSummary) {
        this.professionalSummary = professionalSummary;
    }

    public String getCareerAchievements() { return careerAchievements; }
    public void setCareerAchievements(String careerAchievements) {
        this.careerAchievements = careerAchievements;
    }

}
