package com.hse.Curriculum.Service.Impl;

import com.hse.Curriculum.Mapper.GeoMapper;
import com.hse.Curriculum.Repository.DepartmentRepository;
import com.hse.Curriculum.Repository.MunicipalityRepository;
import com.hse.Curriculum.Repository.CountryRepository;
import com.hse.Curriculum.Dto.GeoDTO.Department.*;
import com.hse.Curriculum.Dto.GeoDTO.Municipality.*;
import com.hse.Curriculum.Models.Country;
import com.hse.Curriculum.Models.Municipality;
import com.hse.Curriculum.Models.Department;
import com.hse.Curriculum.Service.GeoService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GeoServiceImpl implements GeoService {

    private final DepartmentRepository  departmentRepository;
    private final MunicipalityRepository municipalityRepository;
    private final CountryRepository     countryRepository;
    private final GeoMapper             geoMapper;

    // ════════════════════════════════════════════════════════════
    //  DEPARTAMENTOS
    // ════════════════════════════════════════════════════════════

    @Override
    @Transactional
    public DepartmentResponseDTO createDepartment(DepartmentCreateDTO dto) {
        Country country = countryRepository.findById(dto.getCountryId())
                .orElseThrow(() -> new RuntimeException(
                        "País no encontrado con ID: " + dto.getCountryId()));

        if (departmentRepository.existsByNameIgnoreCaseAndCountryCountryId(
                dto.getName(), dto.getCountryId())) {
            throw new RuntimeException(
                    "Ya existe un departamento con el nombre: " + dto.getName());
        }

        Department department = Department.builder()
                .country(country)
                .name(dto.getName())
                .daneCode(dto.getDaneCode())
                .build();

        return geoMapper.toDepartmentResponseDTO(
                departmentRepository.save(department));
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentResponseDTO getDepartmentById(Integer departmentId) {
        return geoMapper.toDepartmentResponseDTO(
                getDepartmentEntityById(departmentId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentResponseDTO> getDepartmentsByCountry(Integer countryId) {
        return geoMapper.toDepartmentResponseDTOList(
                departmentRepository.findByCountryCountryIdOrderByNameAsc(countryId));
    }

    @Override
    @Transactional
    public DepartmentResponseDTO updateDepartment(Integer departmentId, DepartmentUpdateDTO dto) {
        Department department = getDepartmentEntityById(departmentId);

        if (dto.getName() != null)     department.setName(dto.getName());
        if (dto.getDaneCode() != null) department.setDaneCode(dto.getDaneCode());

        if (dto.getCountryId() != null) {
            Country country = countryRepository.findById(dto.getCountryId())
                    .orElseThrow(() -> new RuntimeException(
                            "País no encontrado con ID: " + dto.getCountryId()));
            department.setCountry(country);
        }

        return geoMapper.toDepartmentResponseDTO(
                departmentRepository.save(department));
    }

    @Override
    @Transactional
    public void deleteDepartment(Integer departmentId) {
        Department department = getDepartmentEntityById(departmentId);
        departmentRepository.delete(department);
    }

    @Override
    @Transactional(readOnly = true)
    public Department getDepartmentEntityById(Integer departmentId) {
        return departmentRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException(
                        "Departamento no encontrado con ID: " + departmentId));
    }

    // ════════════════════════════════════════════════════════════
    //  MUNICIPIOS
    // ════════════════════════════════════════════════════════════

    @Override
    @Transactional
    public MunicipalityResponseDTO createMunicipality(MunicipalityCreateDTO dto) {
        Department department = getDepartmentEntityById(dto.getDepartmentId());

        if (municipalityRepository.existsByNameIgnoreCaseAndDepartmentDepartmentId(
                dto.getName(), dto.getDepartmentId())) {
            throw new RuntimeException(
                    "Ya existe un municipio con el nombre: " + dto.getName());
        }

        // Si se marca como capital, desmarcar la capital anterior
        if (Boolean.TRUE.equals(dto.getIsCapital())) {
            municipalityRepository
                    .findByDepartmentDepartmentIdAndIsCapitalTrue(dto.getDepartmentId())
                    .ifPresent(oldCapital -> {
                        oldCapital.setIsCapital(false);
                        municipalityRepository.save(oldCapital);
                    });
        }

        Municipality municipality = Municipality.builder()
                .department(department)
                .name(dto.getName())
                .daneCode(dto.getDaneCode())
                .isCapital(dto.getIsCapital() != null ? dto.getIsCapital() : false)
                .build();

        return geoMapper.toMunicipalityResponseDTO(
                municipalityRepository.save(municipality));
    }

    @Override
    @Transactional(readOnly = true)
    public MunicipalityResponseDTO getMunicipalityById(Integer municipalityId) {
        return geoMapper.toMunicipalityResponseDTO(
                getMunicipalityEntityById(municipalityId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MunicipalityResponseDTO> getMunicipalitiesByDepartment(Integer departmentId) {
        return geoMapper.toMunicipalityResponseDTOList(
                municipalityRepository
                        .findByDepartmentDepartmentIdOrderByNameAsc(departmentId));
    }

    @Override
    @Transactional(readOnly = true)
    public MunicipalityResponseDTO getCapitalByDepartment(Integer departmentId) {
        Municipality capital = municipalityRepository
                .findByDepartmentDepartmentIdAndIsCapitalTrue(departmentId)
                .orElseThrow(() -> new RuntimeException(
                        "No se encontró capital para el departamento ID: " + departmentId));
        return geoMapper.toMunicipalityResponseDTO(capital);
    }

    @Override
    @Transactional
    public MunicipalityResponseDTO updateMunicipality(
            Integer municipalityId, MunicipalityUpdateDTO dto) {

        Municipality municipality = getMunicipalityEntityById(municipalityId);

        if (dto.getName() != null)     municipality.setName(dto.getName());
        if (dto.getDaneCode() != null) municipality.setDaneCode(dto.getDaneCode());

        // Si se cambia a capital, desmarcar la capital anterior del mismo departamento
        if (Boolean.TRUE.equals(dto.getIsCapital())) {
            Integer departmentId = municipality.getDepartment().getDepartmentId();
            municipalityRepository
                    .findByDepartmentDepartmentIdAndIsCapitalTrue(departmentId)
                    .ifPresent(oldCapital -> {
                        if (!oldCapital.getMunicipalityId().equals(municipalityId)) {
                            oldCapital.setIsCapital(false);
                            municipalityRepository.save(oldCapital);
                        }
                    });
            municipality.setIsCapital(true);
        } else if (Boolean.FALSE.equals(dto.getIsCapital())) {
            municipality.setIsCapital(false);
        }

        if (dto.getDepartmentId() != null) {
            Department department = getDepartmentEntityById(dto.getDepartmentId());
            municipality.setDepartment(department);
        }

        return geoMapper.toMunicipalityResponseDTO(
                municipalityRepository.save(municipality));
    }

    @Override
    @Transactional
    public void deleteMunicipality(Integer municipalityId) {
        Municipality municipality = getMunicipalityEntityById(municipalityId);
        municipalityRepository.delete(municipality);
    }

    // ── Helper privado ──────────────────────────────────────────
    private Municipality getMunicipalityEntityById(Integer municipalityId) {
        return municipalityRepository.findById(municipalityId)
                .orElseThrow(() -> new RuntimeException(
                        "Municipio no encontrado con ID: " + municipalityId));
    }
}
