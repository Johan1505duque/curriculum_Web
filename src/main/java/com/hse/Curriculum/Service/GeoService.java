package com.hse.Curriculum.Service;

import com.hse.Curriculum.Dto.GeoDTO.Municipality.*;
import com.hse.Curriculum.Dto.GeoDTO.Department.*;
import com.hse.Curriculum.Models.Department;
import com.hse.Curriculum.Models.Municipality;

import java.util.List;

public interface GeoService {
    // ── Departamentos ──────────────────────────────────────────
    DepartmentResponseDTO    createDepartment(DepartmentCreateDTO dto);
    DepartmentResponseDTO    getDepartmentById(Integer departmentId);
    List<DepartmentResponseDTO> getDepartmentsByCountry(Integer countryId);
    DepartmentResponseDTO    updateDepartment(Integer departmentId, DepartmentUpdateDTO dto);
    void                     deleteDepartment(Integer departmentId);
    Department               getDepartmentEntityById(Integer departmentId);

    // ── Municipios ─────────────────────────────────────────────
    MunicipalityResponseDTO    createMunicipality(MunicipalityCreateDTO dto);
    MunicipalityResponseDTO    getMunicipalityById(Integer municipalityId);
    List<MunicipalityResponseDTO> getMunicipalitiesByDepartment(Integer departmentId);
    MunicipalityResponseDTO    getCapitalByDepartment(Integer departmentId);
    MunicipalityResponseDTO    updateMunicipality(Integer municipalityId, MunicipalityUpdateDTO dto);
    void  deleteMunicipality(Integer municipalityId);
}
