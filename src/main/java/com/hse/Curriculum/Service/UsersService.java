package com.hse.Curriculum.Service;

import com.hse.Curriculum.Repository.UsersRepository;
import com.hse.Curriculum.Models.Users;
import com.hse.Curriculum.Dto.UserDTO.UserSignUpDTO;
import com.hse.Curriculum.Dto.UserDTO.UserResponseDTO;
import com.hse.Curriculum.Security.PasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class UsersService {

    private final UsersRepository usersRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public UsersService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
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
     * Obtener usuario por ID (lanza excepción si no existe)
     */
    public Users getById(Integer userId) {
        return usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    /**
     * Actualizar perfil completo (datos personales)
     */
    @Transactional
    public Users updateProfile(Integer userId,
                               String documentType,
                               String documentNumber,
                               String phoneNumber,
                               LocalDate birthDate) {

        System.out.println("🔄 Actualizando perfil del usuario ID: " + userId);

        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Actualizar solo los campos de perfil
        user.setDocumentType(documentType);
        user.setDocumentNumber(documentNumber);
        user.setPhoneNumber(phoneNumber);
        user.setBirthDate(birthDate);

        Users updatedUser = usersRepository.save(user);
        System.out.println("✅ Perfil actualizado exitosamente");

        return updatedUser;
    }

    /**
     * Registro inicial - Solo datos básicos
     */
    @Transactional
    public UserResponseDTO register(UserSignUpDTO signUpDTO) {
        System.out.println("📝 Registrando nuevo usuario: " + signUpDTO.getEmail());

        // Validar que el email no exista
        if (usersRepository.existsByEmail(signUpDTO.getEmail())) {
            throw new RuntimeException("El email " + signUpDTO.getEmail() + " ya está registrado");
        }

        // Crear usuario con datos mínimos
        Users user = new Users();
        user.setFirstName(signUpDTO.getFirstName());
        user.setLastName(signUpDTO.getLastName());
        user.setEmail(signUpDTO.getEmail().toLowerCase());
        user.setPassword(passwordEncoder.encode(signUpDTO.getPassword()));

        user.setStatus(true); // Activo

        // Guardar
        Users savedUser = usersRepository.save(user);

        System.out.println("✅ Usuario registrado con ID: " + savedUser.getUserId());

        // Retornar respuesta
        return new UserResponseDTO(
                savedUser.getUserId(),
                savedUser.getEmail(),
                "Usuario registrado exitosamente."
        );
    }

    /**
     * Actualizar usuario completo (método genérico)
     */
    @Transactional
    public Users update(Users user) {
        System.out.println("🔄 Actualizando usuario ID: " + user.getUserId());

        if (!usersRepository.existsById(user.getUserId())) {
            throw new RuntimeException("Usuario no encontrado");
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
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

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
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

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
}