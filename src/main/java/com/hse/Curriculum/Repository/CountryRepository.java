package com.hse.Curriculum.Repository;

import com.hse.Curriculum.Models.Country;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CountryRepository extends JpaRepository<Country, Integer> {

    /**
     * Buscar país por código numérico ISO
     */
    Optional<Country> findByCountryCode(Integer countryCode);

    /**
     * Buscar país por código ISO de 2 letras
     */
    Optional<Country> findByIsoCode2(String isoCode2);

    /**
     * Buscar país por código ISO de 3 letras
     */
    Optional<Country> findByIsoCode3(String isoCode3);

    /**
     * Buscar países por nombre (búsqueda parcial, case-insensitive)
     */
    @Query("SELECT c FROM Country c WHERE LOWER(c.countryName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Country> findByCountryNameContaining(@Param("name") String name);

    /**
     * Buscar países por nombre con paginación
     */
    @Query("SELECT c FROM Country c WHERE LOWER(c.countryName) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Country> findByCountryNameContaining(@Param("name") String name, Pageable pageable);

    /**
     * Verificar si existe un país con ese código numérico
     */
    boolean existsByCountryCode(Integer countryCode);

    /**
     * Verificar si existe un país con ese código ISO-2
     */
    boolean existsByIsoCode2(String isoCode2);

    /**
     * Verificar si existe un país con ese código ISO-3
     */
    boolean existsByIsoCode3(String isoCode3);

    /**
     * Obtener todos los países ordenados por nombre
     */
    List<Country> findAllByOrderByCountryNameAsc();
}