package com.hse.Curriculum.Service;

import com.hse.Curriculum.Exception.Users.InvalidChargeAssignmentException;
import com.hse.Curriculum.Exception.Users.UserNotFoundException;
import com.hse.Curriculum.Models.Charge;
import com.hse.Curriculum.Models.Users;
import com.hse.Curriculum.Models.Roles;
import com.hse.Curriculum.Models.Profiles;
import com.hse.Curriculum.Repository.ChargeRepository;
import com.hse.Curriculum.Repository.UsersRepository;
import com.hse.Curriculum.Repository.RolesRepository;
import com.hse.Curriculum.Repository.ProfilesRepository;
import com.hse.Curriculum.Dto.UserDTO.UserSignUpDTO;
import com.hse.Curriculum.Dto.UserDTO.Admin.UserCreateByAdminDTO;
import com.hse.Curriculum.Dto.UserDTO.Admin.UserUpdateByAdminDTO;
import com.hse.Curriculum.Dto.UserDTO.Admin.UserDetailResponseDTO;
import com.hse.Curriculum.Exception.Login.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;
import java.util.List;

@Service
@RequiredArgsConstructor  // ⭐ Lombok genera el constructor automáticamente
public class UsersService {

    // ✅ TODAS las dependencias se inyectan por constructor (gracias a @RequiredArgsConstructor)
    private final UsersRepository usersRepository;
    private final RolesRepository rolesRepository;
    private final PasswordValidator passwordValidator;
    private final PasswordEncoder passwordEncoder;
    private final ChargeRepository chargeRepository;
    private final ProfilesRepository profilesRepository;

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


    /**=============  administrative requests ============================

     /**
     * Crear usuario con rol y cargo (solo para Admin)
     * Los datos personales se guardan en el perfil
     */
    @Transactional
    public Users createUserByAdmin(UserCreateByAdminDTO createDTO) {
        System.out.println("👤 Admin creando usuario: " + createDTO.getEmail());

        // 1. Validar email único
        if (usersRepository.existsByEmail(createDTO.getEmail())) {
            throw new DuplicateEmailException(createDTO.getEmail());
        }

        // 2. Validar número de documento único (buscando en Profiles)
        if (createDTO.getDocumentNumber() != null &&
                profilesRepository.existsByDocumentNumber(createDTO.getDocumentNumber())) {
            throw new RuntimeException(
                    "El número de documento " + createDTO.getDocumentNumber() + " ya está registrado"
            );
        }

        // 3. Validar fortaleza de contraseña
        passwordValidator.validatePassword(
                createDTO.getPassword(),
                createDTO.getFirstName(),
                createDTO.getLastName()
        );

        // 4. Buscar y validar rol
        Roles role = rolesRepository.findById(createDTO.getRoleId())
                .orElseThrow(() -> new RuntimeException(
                        "Rol con ID " + createDTO.getRoleId() + " no encontrado"
                ));

        // 5. Buscar y validar cargo (si se proporciona)
        Charge charge = null;
        if (createDTO.getChargeId() != null) {
            charge = chargeRepository.findById(createDTO.getChargeId())
                    .orElseThrow(() -> new InvalidChargeAssignmentException(createDTO.getChargeId()));

            if (!charge.getStatus()) {
                throw new InvalidChargeAssignmentException(
                        "No se puede asignar un cargo inactivo"
                );
            }
        }

        // 6. Crear usuario (solo datos básicos y autenticación)
        Users user = new Users();
        user.setFirstName(createDTO.getFirstName());
        user.setLastName(createDTO.getLastName());
        user.setEmail(createDTO.getEmail().toLowerCase());
        user.setPassword(passwordEncoder.encode(createDTO.getPassword()));
        user.setRole(role);
        user.setCharge(charge);
        user.setStatus(true);

        // 7. Guardar usuario primero
        Users savedUser = usersRepository.save(user);

        // 8. Crear perfil con datos personales
        Profiles profile = new Profiles();
        profile.setUser(savedUser);
        profile.setDocumentType(createDTO.getDocumentType());
        profile.setDocumentNumber(createDTO.getDocumentNumber());
        profile.setPhoneNumber(createDTO.getPhoneNumber());
        profile.setBirthDate(createDTO.getBirthDate());
        profile.setResidentialAddress(createDTO.getResidentialAddress());

        // 9. Guardar perfil
        Profiles savedProfile = profilesRepository.save(profile);

        // 10. Asociar perfil al usuario
        savedUser.setProfile(savedProfile);

        System.out.println("✅ Usuario creado con ID: " + savedUser.getUserId());
        System.out.println("✅ Perfil creado con ID: " + savedProfile.getProfilesId());

        return savedUser;
    }

    /**
     * Actualizar usuario por Admin
     * Actualiza tanto datos de Users como de Profiles
     */
    @Transactional
    public Users updateUserByAdmin(Integer userId, UserUpdateByAdminDTO updateDTO) {
        System.out.println("🔄 Admin actualizando usuario ID: " + userId);

        // 1. Buscar usuario
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // 2. Obtener o crear perfil
        Profiles profile = user.getProfile();
        if (profile == null) {
            profile = new Profiles();
            profile.setUser(user);
            profile = profilesRepository.save(profile);
            user.setProfile(profile);
        }

        // ========== ACTUALIZAR DATOS DE USERS ==========

        // Actualizar nombre
        if (updateDTO.getFirstName() != null && !updateDTO.getFirstName().isBlank()) {
            user.setFirstName(updateDTO.getFirstName());
        }

        // Actualizar apellido
        if (updateDTO.getLastName() != null && !updateDTO.getLastName().isBlank()) {
            user.setLastName(updateDTO.getLastName());
        }

        // Actualizar email
        if (updateDTO.getEmail() != null && !updateDTO.getEmail().isBlank()) {
            String newEmail = updateDTO.getEmail().toLowerCase();
            if (!newEmail.equals(user.getEmail()) &&
                    usersRepository.existsByEmail(newEmail)) {
                throw new DuplicateEmailException(newEmail);
            }
            user.setEmail(newEmail);
        }


        // Actualizar rol
        if (updateDTO.getRoleId() != null) {
            Roles role = rolesRepository.findById(updateDTO.getRoleId())
                    .orElseThrow(() -> new RuntimeException(
                            "Rol con ID " + updateDTO.getRoleId() + " no encontrado"
                    ));
            user.setRole(role);
        }

        // Actualizar cargo
        if (updateDTO.getChargeId() != null) {
            if (updateDTO.getChargeId() == 0) {
                user.setCharge(null);
            } else {
                Charge charge = chargeRepository.findById(updateDTO.getChargeId())
                        .orElseThrow(() -> new InvalidChargeAssignmentException(updateDTO.getChargeId()));

                if (!charge.getStatus()) {
                    throw new InvalidChargeAssignmentException(
                            "No se puede asignar un cargo inactivo"
                    );
                }
                user.setCharge(charge);
            }
        }

        // Actualizar estado
        if (updateDTO.getStatus() != null) {
            user.setStatus(updateDTO.getStatus());
        }

        // ========== ACTUALIZAR DATOS DE PROFILES ==========

        // Actualizar tipo de documento
        if (updateDTO.getDocumentType() != null && !updateDTO.getDocumentType().isBlank()) {
            profile.setDocumentType(updateDTO.getDocumentType());
        }

        // Actualizar número de documento
        if (updateDTO.getDocumentNumber() != null && !updateDTO.getDocumentNumber().isBlank()) {
            if (!updateDTO.getDocumentNumber().equals(profile.getDocumentNumber()) &&
                    profilesRepository.existsByDocumentNumber(updateDTO.getDocumentNumber())) {
                throw new RuntimeException(
                        "El número de documento " + updateDTO.getDocumentNumber() + " ya está registrado"
                );
            }
            profile.setDocumentNumber(updateDTO.getDocumentNumber());
        }

        // Actualizar teléfono
        if (updateDTO.getPhoneNumber() != null && !updateDTO.getPhoneNumber().isBlank()) {
            profile.setPhoneNumber(updateDTO.getPhoneNumber());
        }

        // Actualizar fecha de nacimiento
        if (updateDTO.getBirthDate() != null) {
            profile.setBirthDate(updateDTO.getBirthDate());
        }

        // Actualizar Direccion de Recidencia
        if (updateDTO.getResidentialAddress() != null) {
            profile.setResidentialAddress(updateDTO.getResidentialAddress());
        }

        // ========== GUARDAR CAMBIOS ==========

        profilesRepository.save(profile);
        Users updatedUser = usersRepository.save(user);

        System.out.println("✅ Usuario actualizado exitosamente");
        System.out.println("✅ Perfil actualizado exitosamente");

        return updatedUser;
    }

    /**
     * Listar todos los usuarios (para Admin)
     */
    public List<Users> getAllUsers() {
        System.out.println("📋 Obteniendo todos los usuarios");
        return usersRepository.findAll();
    }

    /**
     * Listar usuarios por rol
     */
    public List<Users> getUsersByRole(Integer roleId) {
        System.out.println("📋 Obteniendo usuarios con rol ID: " + roleId);
        return usersRepository.findByRole_RoleId(roleId);
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
     * Listar usuarios activos
     */
    public List<Users> getActiveUsers() {
        System.out.println("📋 Obteniendo usuarios activos");
        return usersRepository.findByStatusTrue();
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