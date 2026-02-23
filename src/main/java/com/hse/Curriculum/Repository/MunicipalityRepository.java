package com.hse.Curriculum.Repository;

import com.hse.Curriculum.Models.Municipality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MunicipalityRepository extends JpaRepository <Municipality,Integer> {

    // Todos los municipios de un departamento ordenados por nombre
    List<Municipality> findByDepartmentDepartmentIdOrderByNameAsc(Integer departmentId);

    // Obtener la capital de un departamento
    Optional<Municipality> findByDepartmentDepartmentIdAndIsCapitalTrue(Integer departmentId);

    // Verificar si ya existe un municipio con ese nombre en el departamento
    boolean existsByNameIgnoreCaseAndDepartmentDepartmentId(String name, Integer departmentId);

    // Buscar por código DANE
    Optional<Municipality> findByDaneCode(String daneCode);
}
