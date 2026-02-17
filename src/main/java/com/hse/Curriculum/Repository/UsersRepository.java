package com.hse.Curriculum.Repository;

import com.hse.Curriculum.Models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface UsersRepository extends JpaRepository<Users, Integer> {
    Optional<Users> findByEmail(String email);
    boolean existsByEmail(String email);

    /**
     * Buscar usuarios activos
     */
    List<Users> findByStatusTrue();

    /**
     * Buscar usuarios por rol
     */
    List<Users> findByRole_RoleId(Integer roleId);

    /**
     * Buscar usuarios por nombre, apellido o email (búsqueda parcial)
     */
    List<Users> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String firstName, String lastName, String email
    );
}

