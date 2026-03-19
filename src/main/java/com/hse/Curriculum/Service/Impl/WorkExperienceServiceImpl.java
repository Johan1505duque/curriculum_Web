package com.hse.Curriculum.Service.Impl;

import com.hse.Curriculum.Dto.WorkExperienceDTO.*;
import com.hse.Curriculum.Exception.WorkExperience.*;
import com.hse.Curriculum.Models.*;
import com.hse.Curriculum.Repository.*;
import com.hse.Curriculum.Service.WorkExperienceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class WorkExperienceServiceImpl implements WorkExperienceService {

    private final WorkExperienceRepository workExperienceRepository;
    private final UsersRepository usersRepository;
    private final CountryRepository countryRepository;
    private final DepartmentRepository departmentRepository;
    private final MunicipalityRepository municipalityRepository;

    @Override
    @Transactional
    public WorkExperienceResponseDTO createWorkExperience(Integer userId, WorkExperienceCreateDTO dto) {
        // Validar fechas
        validateDates(dto.getStartDate(), dto.getEndDate(), dto.getCurrentlyWorking());

        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + userId));

        Country country = countryRepository.findById(dto.getCountryId())
                .orElseThrow(() -> new RuntimeException("País no encontrado con ID: " + dto.getCountryId()));

        Department department = null;
        if (dto.getDepartmentId() != null) {
            department = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Departamento no encontrado con ID: " + dto.getDepartmentId()));
        }

        Municipality municipality = null;
        if (dto.getMunicipalityId() != null) {
            municipality = municipalityRepository.findById(dto.getMunicipalityId())
                    .orElseThrow(() -> new RuntimeException("Municipio no encontrado con ID: " + dto.getMunicipalityId()));
        }

        WorkExperience workExperience = WorkExperience.builder()
                .user(user)
                .companyName(dto.getCompanyName())
                .jobTitle(dto.getJobTitle())
                .country(country)
                .department(department)
                .municipality(municipality)
                .functions(dto.getFunctions())
                .startDate(dto.getStartDate())
                .endDate(dto.getCurrentlyWorking() ? null : dto.getEndDate())
                .currentlyWorking(dto.getCurrentlyWorking())
                .build();

        WorkExperience saved = workExperienceRepository.save(workExperience);
        return toResponseDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkExperienceResponseDTO> getWorkExperiencesByUserId(Integer userId) {
        return workExperienceRepository.findByUserUserIdOrderByStartDateDesc(userId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public WorkExperienceResponseDTO getWorkExperienceById(Integer workExperienceId, Integer userId, boolean isAdmin) {
        WorkExperience workExperience = getWorkExperienceEntityById(workExperienceId);

        if (!isAdmin && !workExperience.getUser().getUserId().equals(userId)) {
            throw new WorkExperienceNotFoundException(workExperienceId);
        }

        return toResponseDTO(workExperience);
    }

    @Override
    @Transactional
    public WorkExperienceResponseDTO updateWorkExperience(Integer workExperienceId, Integer userId, WorkExperienceUpdateDTO dto) {
        WorkExperience workExperience = getWorkExperienceEntityById(workExperienceId);

        // Determinar valores finales para validación
        LocalDate finalStartDate = dto.getStartDate() != null ? dto.getStartDate() : workExperience.getStartDate();
        LocalDate finalEndDate = dto.getEndDate() != null ? dto.getEndDate() : workExperience.getEndDate();
        Boolean finalCurrentlyWorking = dto.getCurrentlyWorking() != null ? dto.getCurrentlyWorking() : workExperience.getCurrentlyWorking();

        validateDates(finalStartDate, finalEndDate, finalCurrentlyWorking);

        // Actualizar solo campos no nulos
        if (dto.getCompanyName() != null) workExperience.setCompanyName(dto.getCompanyName());
        if (dto.getJobTitle() != null) workExperience.setJobTitle(dto.getJobTitle());
        if (dto.getFunctions() != null) workExperience.setFunctions(dto.getFunctions());
        if (dto.getStartDate() != null) workExperience.setStartDate(dto.getStartDate());
        if (dto.getCurrentlyWorking() != null) {
            workExperience.setCurrentlyWorking(dto.getCurrentlyWorking());
            if (dto.getCurrentlyWorking()) {
                workExperience.setEndDate(null);
            } else if (dto.getEndDate() != null) {
                workExperience.setEndDate(dto.getEndDate());
            }
        } else if (dto.getEndDate() != null) {
            workExperience.setEndDate(dto.getEndDate());
        }

        if (dto.getCountryId() != null) {
            Country country = countryRepository.findById(dto.getCountryId())
                    .orElseThrow(() -> new RuntimeException("País no encontrado con ID: " + dto.getCountryId()));
            workExperience.setCountry(country);
        }

        if (dto.getDepartmentId() != null) {
            Department department = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Departamento no encontrado con ID: " + dto.getDepartmentId()));
            workExperience.setDepartment(department);
        }

        if (dto.getMunicipalityId() != null) {
            Municipality municipality = municipalityRepository.findById(dto.getMunicipalityId())
                    .orElseThrow(() -> new RuntimeException("Municipio no encontrado con ID: " + dto.getMunicipalityId()));
            workExperience.setMunicipality(municipality);
        }

        return toResponseDTO(workExperienceRepository.save(workExperience));
    }

    @Override
    @Transactional
    public void deleteWorkExperience(Integer workExperienceId, Integer userId, boolean isAdmin) {
        WorkExperience workExperience = getWorkExperienceEntityById(workExperienceId);

        if (!isAdmin && !workExperience.getUser().getUserId().equals(userId)) {
            throw new WorkExperienceUnauthorizedException("No autorizado para eliminar este registro");
        }

        workExperienceRepository.delete(workExperience);
    }

    @Override
    @Transactional(readOnly = true)
    public WorkExperience getWorkExperienceEntityById(Integer workExperienceId) {
        return workExperienceRepository.findById(workExperienceId)
                .orElseThrow(() -> new WorkExperienceNotFoundException(workExperienceId));
    }

    // ─── Helpers ────────────────────────────────────────────────────────────────

    private void validateDates(java.time.LocalDate startDate, java.time.LocalDate endDate, Boolean currentlyWorking) {
        if (Boolean.TRUE.equals(currentlyWorking) && endDate != null) {
            throw new InvalidWorkExperienceDateException(
                    "Si actualmente está laborando, no debe ingresar una fecha de fin");
        }
        if (!Boolean.TRUE.equals(currentlyWorking) && endDate != null && startDate != null) {
            if (endDate.isBefore(startDate)) {
                throw new InvalidWorkExperienceDateException(
                        "La fecha de fin no puede ser anterior a la fecha de inicio");
            }
        }
    }

    private WorkExperienceResponseDTO toResponseDTO(WorkExperience we) {
        return WorkExperienceResponseDTO.builder()
                .workExperienceId(we.getWorkExperienceId())
                .userId(we.getUser().getUserId())
                .companyName(we.getCompanyName())
                .jobTitle(we.getJobTitle())
                .countryId(we.getCountry() != null ? we.getCountry().getCountryId() : null)
                .countryName(we.getCountry() != null ? we.getCountry().getCountryName() : null)
                .departmentId(we.getDepartment() != null ? we.getDepartment().getDepartmentId() : null)
                .departmentName(we.getDepartment() != null ? we.getDepartment().getName() : null)
                .municipalityId(we.getMunicipality() != null ? we.getMunicipality().getMunicipalityId() : null)
                .municipalityName(we.getMunicipality() != null ? we.getMunicipality().getName() : null)
                .functions(we.getFunctions())
                .startDate(we.getStartDate())
                .endDate(we.getEndDate())
                .currentlyWorking(we.getCurrentlyWorking())
                .build();
    }
}
