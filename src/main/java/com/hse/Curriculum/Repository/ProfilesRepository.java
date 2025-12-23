package com.hse.Curriculum.Repository;

import com.hse.Curriculum.Models.Profiles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ProfilesRepository extends JpaRepository<Profiles, Integer> {

    // Buscar perfil por ID de usuario
    Optional<Profiles> findByUser_UserId(Integer userId);

    // Verificar si existe perfil para un usuario
    boolean existsByUser_UserId(Integer userId);

    // Verificar si existe número de documento (para evitar duplicados)
    boolean existsByDocumentNumber(String documentNumber);

    // Buscar por número de documento
    Optional<Profiles> findByDocumentNumber(String documentNumber);
}
