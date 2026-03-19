package com.hse.Curriculum.Exception.WorkExperience;

public class WorkExperienceNotFoundException extends RuntimeException {
    public WorkExperienceNotFoundException(Integer Id) {
        super("Registro de experiencia laboral  no encontrado");
    }
}
