package com.hse.Curriculum.Repository;

import com.hse.Curriculum.Models.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository  extends JpaRepository <Department,Integer> {
    // Todos los departamentos de un país ordenados por nombre
    List<Department> findByCountryCountryIdOrderByNameAsc(Integer countryId);

    // Verificar si ya existe un departamento con ese nombre en el país
    boolean existsByNameIgnoreCaseAndCountryCountryId(String name, Integer countryId);

    // Buscar por código DANE
    Optional<Department> findByDaneCode(String daneCode);
}
