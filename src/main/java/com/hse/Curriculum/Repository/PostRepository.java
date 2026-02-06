package com.hse.Curriculum.Repository;

import com.hse.Curriculum.Models.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Post (Cargos)
 */
@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

    /**
     * Buscar cargo por nombre
     */
    Optional<Post> findByNamePost(String namePost);

    /**
     * Verificar si existe un cargo con ese nombre
     */
    boolean existsByNamePost(String namePost);

    /**
     * Verificar si existe un cargo con ese nombre excluyendo un ID específico
     */
    boolean existsByNamePostAndPostIdNot(String namePost, Integer postId);

    /**
     * Listar todos los cargos activos
     */
    List<Post> findByStatusTrue();

    /**
     * Listar todos los cargos inactivos
     */
    List<Post> findByStatusFalse();

    /**
     * Buscar cargos por nombre que contenga un texto (búsqueda parcial)
     */
    List<Post> findByNamePostContainingIgnoreCase(String namePost);
}
