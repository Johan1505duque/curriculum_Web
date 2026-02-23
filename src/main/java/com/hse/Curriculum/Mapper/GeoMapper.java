package com.hse.Curriculum.Mapper;

import com.hse.Curriculum.Dto.GeoDTO.Department.*;
import com.hse.Curriculum.Dto.GeoDTO.Municipality.*;
import com.hse.Curriculum.Models.Department;
import com.hse.Curriculum.Models.Municipality;
import org.hibernate.annotations.Comment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;


@Component

public class GeoMapper {
    public DepartmentResponseDTO toDepartmentResponseDTO(Department department) {
        if (department == null) return null;

        return DepartmentResponseDTO.builder()
                .departmentId(department.getDepartmentId())
                .countryId(department.getCountry() != null
                        ? department.getCountry().getCountryId() : null)
                .countryName(department.getCountry() != null
                        ? department.getCountry().getCountryName() : null)
                .name(department.getName())
                .daneCode(department.getDaneCode())
                .build();
    }

    public List<DepartmentResponseDTO> toDepartmentResponseDTOList(List<Department> departments) {
        if (departments == null) return List.of();
        return departments.stream()
                .map(this::toDepartmentResponseDTO)
                .collect(Collectors.toList());
    }

    public MunicipalityResponseDTO toMunicipalityResponseDTO(Municipality municipality) {
        if (municipality == null) return null;

        return MunicipalityResponseDTO.builder()
                .municipalityId(municipality.getMunicipalityId())
                .departmentId(municipality.getDepartment() != null
                        ? municipality.getDepartment().getDepartmentId() : null)
                .departmentName(municipality.getDepartment() != null
                        ? municipality.getDepartment().getName() : null)
                .countryId(municipality.getDepartment() != null
                        && municipality.getDepartment().getCountry() != null
                        ? municipality.getDepartment().getCountry().getCountryId() : null)
                .countryName(municipality.getDepartment() != null
                        && municipality.getDepartment().getCountry() != null
                        ? municipality.getDepartment().getCountry().getCountryName() : null)
                .name(municipality.getName())
                .daneCode(municipality.getDaneCode())
                .isCapital(municipality.getIsCapital())
                .build();
    }

    public List<MunicipalityResponseDTO> toMunicipalityResponseDTOList(List<Municipality> municipalities) {
        if (municipalities == null) return List.of();
        return municipalities.stream()
                .map(this::toMunicipalityResponseDTO)
                .collect(Collectors.toList());
    }
}
