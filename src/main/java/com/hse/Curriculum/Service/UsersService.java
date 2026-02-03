package com.hse.Curriculum.Service;

import com.hse.Curriculum.Exception.Users.UserNotFoundException;
import com.hse.Curriculum.Repository.UsersRepository;
import com.hse.Curriculum.Models.Users;
import com.hse.Curriculum.Models.Roles;
import com.hse.Curriculum.Repository.RolesRepository;
import com.hse.Curriculum.Dto.UserDTO.UserSignUpDTO;
import com.hse.Curriculum.Exception.Login.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

@Service
@RequiredArgsConstructor  // ⭐ Lombok genera el constructor automáticamente
public class UsersService {

    // ✅ TODAS las dependencias se inyectan por constructor (gracias a @RequiredArgsConstructor)
    private final UsersRepository usersRepository;
    private final RolesRepository rolesRepository;
    private final PasswordValidator passwordValidator;
    private final PasswordEncoder passwordEncoder;

    /**
     * Registro inicial - Solo datos básicos

     */
    @Transactional
    public Users register(UserSignUpDTO signUpDTO) {

        // 1. ✅ VALIDAR QUE EL EMAIL NO ESTÉ DUPLICADO
        if (usersRepository.existsByEmail(signUpDTO.getEmail())) {
            throw new DuplicateEmailException(signUpDTO.getEmail());
        }

        // 2. ✅ VALIDAR FORTALEZA DE LA CONTRASEÑA
        passwordValidator.validatePassword(
                signUpDTO.getPassword(),
                signUpDTO.getFirstName(),
                signUpDTO.getLastName()
        );

        // 3. 🔥 BUSCAR EL ROL "USER" POR DEFECTO
        Roles userRole = rolesRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException(
                        "Error del sistema: Rol USER no encontrado. Contacte al administrador."
                ));

        // 4. Crear el usuario
        Users user = new Users();
        user.setFirstName(signUpDTO.getFirstName());
        user.setLastName(signUpDTO.getLastName());
        user.setEmail(signUpDTO.getEmail().toLowerCase());
        user.setPassword(passwordEncoder.encode(signUpDTO.getPassword()));
        user.setStatus(true);
        user.setRole(userRole);

        // 5. Guardar en la base de datos
        return usersRepository.save(user);
    }

    /**
     * Buscar usuario por ID
     */
    public Optional<Users> findById(Integer userId) {
        System.out.println("🔍 Buscando usuario con ID: " + userId);
        Optional<Users> user = usersRepository.findById(userId);

        if (user.isPresent()) {
            System.out.println("✅ Usuario encontrado: " + user.get().getEmail());
        } else {
            System.out.println("⚠️ Usuario con ID " + userId + " no existe");
        }

        return user;
    }

    /**
     * Buscar usuario por email
     */
    public Optional<Users> findByEmail(String email) {
        System.out.println("🔍 Buscando usuario con email: " + email);
        return usersRepository.findByEmail(email);
    }

    /**
     * Obtener usuario por ID (lanza excepción si no existe)
     */
    public Users getById(Integer userId) {
        return usersRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    /**
     * Actualizar usuario completo (método genérico)
     */
    @Transactional
    public Users update(Users user) {
        System.out.println("🔄 Actualizando usuario ID: " + user.getUserId());

        if (!usersRepository.existsById(user.getUserId())) {
            throw new UserNotFoundException(user.getUserId());
        }

        return usersRepository.save(user);
    }

    /**
     * Deshabilitar usuario (soft delete)
     */
    @Transactional
    public void disableUser(Integer userId) {
        System.out.println("🔄 Deshabilitando usuario ID: " + userId);

        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        user.setStatus(false);
        usersRepository.save(user);

        System.out.println("✅ Usuario deshabilitado exitosamente");
    }

    /**
     * Habilitar usuario
     */
    @Transactional
    public void enableUser(Integer userId) {
        System.out.println("🔄 Habilitando usuario ID: " + userId);

        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        user.setStatus(true);
        usersRepository.save(user);

        System.out.println("✅ Usuario habilitado exitosamente");
    }

    /**
     * Verificar si existe usuario por ID
     */
    public boolean existsById(Integer userId) {
        return usersRepository.existsById(userId);
    }

    /**
     * Verificar si existe usuario por email
     */
    public boolean existsByEmail(String email) {
        return usersRepository.existsByEmail(email);
    }

    public UserDetails loadUserByEmail(String email) {
        Users user = findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().getName())
                .build();
    }
}