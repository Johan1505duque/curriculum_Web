package com.hse.Curriculum.Service;
import com.hse.Curriculum.Dto.EducationDTO.EducationCreateDTO;
import com.hse.Curriculum.Dto.EducationDTO.EducationResponseDTO;
import com.hse.Curriculum.Dto.EducationDTO.EducationUpdateDTO;
import com.hse.Curriculum.Exception.Education.*;
import com.hse.Curriculum.Models.Country;
import com.hse.Curriculum.Models.Education;
import com.hse.Curriculum.Models.Users;
import com.hse.Curriculum.Repository.CountryRepository;
import com.hse.Curriculum.Repository.EducationRepository;
import com.hse.Curriculum.Repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EducationService {
    private final EducationRepository educationRepository;
    private final UsersRepository usersRepository;
    private final CountryRepository countryRepository;

    /**
     * Crear un nuevo registro de educación
     */
    @Transactional
    public EducationResponseDTO createEducation(Integer userId, EducationCreateDTO dto) {
        // Validar usuario
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Validar fecha
        validateEducationDate(dto.getLastLevelDate());

        // Validar y obtener país si se proporciona
        Country country = null;
        if (dto.getCountryCode() != null) {
            country = countryRepository.findByCountryCode(dto.getCountryCode())
                    .orElseThrow(() -> new RuntimeException("País con Codigo "+ dto.getCountryCode()+ "no encontrado"));
        }

        // Crear entidad
        Education education = Education.builder()
                .user(user)
                .typeEducation(dto.getTypeEducation())
                .academicLevel(dto.getAcademicLevel())
                .institutionName(dto.getInstitutionName())
                .degreeTitle(dto.getDegreeTitle())
                .country(country)
                .graduateStatus(dto.getGraduateStatus() != null ? dto.getGraduateStatus() : null)
                .lastLevelCompleted(dto.getLastLevelCompleted())
                .lastLevelDate(dto.getLastLevelDate())
                .build();

        Education saved = educationRepository.save(education);
        return mapToResponseDTO(saved);
    }

    /**
     * Obtener todos los registros de educación de un usuario
     */
    @Transactional(readOnly = true)
    public List<EducationResponseDTO> getEducationsByUserId(Integer userId) {
        // Verificar que el usuario existe
        if (!usersRepository.existsById(userId)) {
            throw new RuntimeException("Usuario no encontrado");
        }

        List<Education> educations = educationRepository.findByUserIdWithCountry(userId);
        return educations.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtener un registro específico de educación
     */
    @Transactional(readOnly = true)
    public EducationResponseDTO getEducationById(Integer educationId, Integer userId, boolean isAdmin) {
        Education education;

        if (isAdmin) {
            // Admin puede ver cualquier registro
            education = educationRepository.findByIdWithDetails(educationId)
                    .orElseThrow(() -> new EducationNotFoundException("Registro de educación no encontrado"));
        } else {
            // Usuario normal solo puede ver sus propios registros
            education = educationRepository.findByIdAndUserId(educationId, userId)
                    .orElseThrow(() -> new EducationNotFoundException("Registro de educación no encontrado o no autorizado"));
        }

        return mapToResponseDTO(education);
    }

    /**
     * Actualizar un registro de educación (PATCH)
     */
    @Transactional
    public EducationResponseDTO updateEducation(Integer educationId, Integer userId, EducationUpdateDTO dto) {
        // Verificar que el registro pertenece al usuario
        Education education = educationRepository.findByIdAndUserId(educationId, userId)
                .orElseThrow(() -> new EducationNotFoundException("Registro de educación no encontrado o no autorizado"));

        // Validar fecha si se proporciona
        if (dto.getLastLevelDate() != null) {
            validateEducationDate(dto.getLastLevelDate());
        }

        // Actualizar campos solo si vienen en el DTO
        if (dto.getTypeEducation() != null) {
            education.setTypeEducation(dto.getTypeEducation());
        }
        if (dto.getAcademicLevel() != null) {
            education.setAcademicLevel(dto.getAcademicLevel());
        }
        if (dto.getInstitutionName() != null) {
            education.setInstitutionName(dto.getInstitutionName());
        }
        if (dto.getDegreeTitle() != null) {
            education.setDegreeTitle(dto.getDegreeTitle());
        }
        if (dto.getCountryCode() != null) {
            Country country = countryRepository.findByCountryCode(dto.getCountryCode())
                    .orElseThrow(() -> new RuntimeException("País con codigo "+ dto.getCountryCode() +"no fue encontrado"));
            education.setCountry(country);
        }
        if (dto.getGraduateStatus() != null) {
            education.setGraduateStatus(dto.getGraduateStatus());
        }
        if (dto.getLastLevelCompleted() != null) {
            education.setLastLevelCompleted(dto.getLastLevelCompleted());
        }
        if (dto.getLastLevelDate() != null) {
            education.setLastLevelDate(dto.getLastLevelDate());
        }

        Education updated = educationRepository.save(education);
        return mapToResponseDTO(updated);
    }

    /**
     * Eliminar un registro de educación
     */
    @Transactional
    public void deleteEducation(Integer educationId, Integer userId, boolean isAdmin) {
        Education education;

        if (isAdmin) {
            // Admin puede eliminar cualquier registro
            education = educationRepository.findById(educationId)
                    .orElseThrow(() -> new EducationNotFoundException("Registro de educación no encontrado"));
        } else {
            // Usuario normal solo puede eliminar sus propios registros
            education = educationRepository.findByIdAndUserId(educationId, userId)
                    .orElseThrow(() -> new EducationNotFoundException("Registro de educación no encontrado o no autorizado"));
        }

        educationRepository.delete(education);
    }

    /**
     * Validar que la fecha de educación no sea futura
     */
    private void validateEducationDate(LocalDate date) {
        if (date != null && date.isAfter(LocalDate.now())) {
            throw new InvalidEducationDateException("La fecha del último nivel no puede ser futura");
        }
    }

    /**
     * Mapear entidad a DTO de respuesta
     */
    private EducationResponseDTO mapToResponseDTO(Education education) {
        return EducationResponseDTO.builder()
                .educationId(education.getEducationId())
                .userId(education.getUser().getUserId())
                .typeEducation(education.getTypeEducation())
                .academicLevel(education.getAcademicLevel())
                .institutionName(education.getInstitutionName())
                .degreeTitle(education.getDegreeTitle())
                .countryCode(education.getCountry() != null ? education.getCountry().getCountryCode() : null)
                .countryName(education.getCountry() != null ? education.getCountry().getCountryName() : null)
                .graduateStatus(education.getGraduateStatus())
                .lastLevelCompleted(education.getLastLevelCompleted())
                .lastLevelDate(education.getLastLevelDate())
                .createdAt(education.getCreatedAt())
                .updatedAt(education.getUpdatedAt())
                .build();
    }

    /**
     * Obtener educación por ID sin verificación de usuario (para auditoría)
     */
    @Transactional(readOnly = true)
    public Education getEducationEntityById(Integer educationId) {
        return educationRepository.findById(educationId)
                .orElseThrow(() -> new EducationNotFoundException("Registro de educación no encontrado"));
    }
}
