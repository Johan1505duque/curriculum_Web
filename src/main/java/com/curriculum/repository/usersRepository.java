package com.curriculum.repository;

import com.curriculum.models.users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface usersRepository extends JpaRepository<users, Integer> {

    // Spring Data JPA crea automáticamente estos métodos
    Optional<users> findByEmail(String email);

    boolean existsByEmail(String email);
}