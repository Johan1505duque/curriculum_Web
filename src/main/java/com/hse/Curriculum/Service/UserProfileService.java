package com.hse.Curriculum.Service;

import com.hse.Curriculum.Dto.ProfileDTO.PerfileRegisterDTO;
import com.hse.Curriculum.Dto.ProfileDTO.ProfileResponseDTO;
import com.hse.Curriculum.Dto.ProfileDTO.ProfileUpdateDTO;
import com.hse.Curriculum.Dto.ProfileDTO.UserProfileDTO;

import com.hse.Curriculum.Exception.Profile.DuplicateDocumentException;
import com.hse.Curriculum.Exception.Profile.ProfileNotFoundException;

import com.hse.Curriculum.Models.Profiles;
import com.hse.Curriculum.Models.Users;
import com.hse.Curriculum.Repository.ProfilesRepository;
import com.hse.Curriculum.Repository.UsersRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserProfileService {
    private final UsersRepository usersRepository;
    private final ProfilesRepository profilesRepository;

    public UserProfileService(UsersRepository usersRepository,
                              ProfilesRepository profilesRepository) {
        this.usersRepository = usersRepository;
        this.profilesRepository = profilesRepository;
    }

    /**
     * Obtener información completa del usuario (Users + Profiles)
     */
    @Transactional(readOnly = true)
    public UserProfileDTO getUserProfile(Integer userId) {
        System.out.println("🔍 Consultando información completa del usuario ID: " + userId);

        // Buscar usuario
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + userId));

        // Buscar perfil (puede no existir)
        Optional<Profiles> profileOpt = profilesRepository.findByUser_UserId(userId);

        // Mapear a DTO
        UserProfileDTO dto = new UserProfileDTO();

        // Datos de Users
        dto.setUserId(user.getUserId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());

        // Datos de Profiles (si existe)
        if (profileOpt.isPresent()) {
            Profiles profile = profileOpt.get();
            dto.setDocumentType(profile.getDocumentType());
            dto.setDocumentNumber(profile.getDocumentNumber());
            dto.setPhoneNumber(profile.getPhoneNumber());
            dto.setResidentialAddress(profile.getResidentialAddress());
            dto.setBirthDate(profile.getBirthDate());

        }

        System.out.println("✅ Información obtenida exitosamente");
        return dto;
    }

    /**
     * Actualizar datos completos (Users + Profiles) excepto email y password
     * Usa tu ProfileResponseDTO existente
     */
    @Transactional
    public ProfileResponseDTO updateCompleteProfile(Integer userId, ProfileUpdateDTO updateDTO) {

        System.out.println("🔄 Actualizando datos completos del usuario ID: " + userId);

        // 1. Actualizar USERS (firstName, lastName)
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + userId));

        user.setFirstName(updateDTO.getFirstName());
        user.setLastName(updateDTO.getLastName());
        // Email y password NO se tocan

        Users updatedUser = usersRepository.save(user);
        System.out.println("✅ Usuario actualizado");

        // 2. Actualizar PROFILES
        Profiles profile = profilesRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new ProfileNotFoundException(userId));

        // Verificar documento duplicado (excepto el propio)
        if (!profile.getDocumentNumber().equals(updateDTO.getDocumentNumber()) &&
                profilesRepository.existsByDocumentNumber(updateDTO.getDocumentNumber())) {
            throw new DuplicateDocumentException(updateDTO.getDocumentNumber());
        }

        profile.setDocumentType(updateDTO.getDocumentType());
        profile.setDocumentNumber(updateDTO.getDocumentNumber());
        profile.setPhoneNumber(updateDTO.getPhoneNumber());
        profile.setResidentialAddress(updateDTO.getResidentialAddress());
        profile.setBirthDate(updateDTO.getBirthDate());

        Profiles updatedProfile = profilesRepository.save(profile);
        System.out.println("✅ Perfil actualizado");

        // 3. Mapear a tu ProfileResponseDTO existente
        ProfileResponseDTO response = new ProfileResponseDTO();
        response.setUserId(updatedUser.getUserId());
        response.setFirstName(updatedUser.getFirstName());
        response.setLastName(updatedUser.getLastName());
        response.setEmail(updatedUser.getEmail());
        response.setDocumentType(updatedProfile.getDocumentType());
        response.setDocumentNumber(updatedProfile.getDocumentNumber());
        response.setPhoneNumber(updatedProfile.getPhoneNumber());
        response.setResidentialAddress(updatedProfile.getResidentialAddress());
        response.setBirthDate(updatedProfile.getBirthDate());

        System.out.println("🎉 Actualización completa exitosa");

        return response;
    }

    /**
     * Registrar usuario con perfil completo en una sola transacción
     */
    @Transactional
    public ProfileResponseDTO registerUserWithProfile(
            PerfileRegisterDTO registrationDTO) {

        System.out.println("🆕 Iniciando registro completo de usuario...");

        // 1. Validar que el email no exista
        if (usersRepository.existsByEmail(registrationDTO.getEmail())) {
            throw new RuntimeException("El email " + registrationDTO.getEmail() + " ya está registrado");
        }

        // 2. Validar que el documento no exista
        if (profilesRepository.existsByDocumentNumber(registrationDTO.getDocumentNumber())) {
            throw new DuplicateDocumentException(registrationDTO.getDocumentNumber());
        }

        // 3. Crear usuario (tabla Users)
        Users user = new Users();
        user.setFirstName(registrationDTO.getFirstName());
        user.setLastName(registrationDTO.getLastName());
        user.setEmail(registrationDTO.getEmail());
        user.setPassword(registrationDTO.getPassword());
        user.setStatus(true);

        Users savedUser = usersRepository.save(user);
        System.out.println("✅ Usuario creado con ID: " + savedUser.getUserId());

        // 4. Crear perfil (tabla Profiles)
        Profiles profile = new Profiles();
        profile.setUser(savedUser);
        profile.setDocumentType(registrationDTO.getDocumentType());
        profile.setDocumentNumber(registrationDTO.getDocumentNumber());
        profile.setPhoneNumber(registrationDTO.getPhoneNumber());
        profile.setResidentialAddress(registrationDTO.getResidentialAddress());
        profile.setBirthDate(registrationDTO.getBirthDate());



        Profiles savedProfile = profilesRepository.save(profile);
        System.out.println("✅ Perfil creado con ID: " + savedProfile.getProfilesId());

        // 5. Construir respuesta
        ProfileResponseDTO response = new ProfileResponseDTO();

        response.setUserId(savedUser.getUserId());
        response.setFirstName(savedUser.getFirstName());
        response.setLastName(savedUser.getLastName());
        response.setEmail(savedUser.getEmail());
        response.setPassword(savedUser.getPassword());
        response.setDocumentType(savedProfile.getDocumentType());
        response.setDocumentNumber(savedProfile.getDocumentNumber());
        response.setPhoneNumber(savedProfile.getPhoneNumber());
        response.setResidentialAddress(savedProfile.getResidentialAddress());
        response.setBirthDate(savedProfile.getBirthDate());

        System.out.println("🎉 Registro completo exitoso para: " +
                savedUser.getFirstName() + " " + savedUser.getLastName());

        return response;
    }
}
