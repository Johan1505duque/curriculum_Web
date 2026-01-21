package com.hse.Curriculum.Repository;
import com.hse.Curriculum.Models.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CountryRepository extends JpaRepository<Country, Integer> {
    /**
     * Buscar país por nombre exacto (case insensitive)
     */
    @Query("SELECT c FROM Country c WHERE LOWER(c.countryName) = LOWER(:countryName)")
    Optional<Country> findByCountryNameIgnoreCase(@Param("countryName") String countryName);

    /**
     * Buscar países que contengan un texto en el nombre
     */
    @Query("SELECT c FROM Country c WHERE LOWER(c.countryName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) ORDER BY c.countryName")
    List<Country> searchByCountryName(@Param("searchTerm") String searchTerm);

    /**
     * Verificar si existe un país por nombre
     */
    boolean existsByCountryNameIgnoreCase(String countryName);

    /**
     * Obtener todos los países ordenados alfabéticamente
     */
    @Query("SELECT c FROM Country c ORDER BY c.countryName ASC")
    List<Country> findAllOrderedByName();

    /**
     * Contar cuántos países hay registrados
     */
    @Query("SELECT COUNT(c) FROM Country c")
    Long countAllCountries();
}
