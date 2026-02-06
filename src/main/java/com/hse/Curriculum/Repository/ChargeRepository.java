package com.hse.Curriculum.Repository;

import com.hse.Curriculum.Models.Charge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Post (Cargos)
 */
@Repository
public interface ChargeRepository extends JpaRepository<Charge, Integer> {

    /**
     * Buscar cargo por nombre
     */
    Optional<Charge> findByNameCharge(String namePost);

    /**
     * Verificar si existe un cargo con ese nombre
     */
    boolean existsByNameCharge(String namePost);

    /**
     * Verificar si existe un cargo con ese nombre excluyendo un ID específico
     */
    boolean existsByNameChargeAndChargeIdNot(String namePost, Integer postId);

    /**
     * Listar todos los cargos activos
     */
    List<Charge> findByStatusTrue();

    /**
     * Listar todos los cargos inactivos
     */
    List<Charge> findByStatusFalse();

    /**
     * Buscar cargos por nombre que contenga un texto (búsqueda parcial)
     */
    List<Charge> findByNameChargeContainingIgnoreCase(String namePost);
}
