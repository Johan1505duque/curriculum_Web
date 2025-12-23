package com.hse.Curriculum.Service;

import com.hse.Curriculum.Dto.ProfileDTO.RegisterPerfileDTO;
import com.hse.Curriculum.Dto.ProfileDTO.ProfessionalProfileUpdateDTO;
import com.hse.Curriculum.Dto.ProfileDTO.ProfileResponseDTO;
import com.hse.Curriculum.Exception.Profile.*;
import com.hse.Curriculum.Models.Profiles;
import com.hse.Curriculum.Models.Users;
import com.hse.Curriculum.Repository.ProfilesRepository;
import com.hse.Curriculum.Repository.UsersRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfilesService {

    private final ProfilesRepository profilesRepository;
    private final UsersRepository usersRepository;

    public ProfilesService(ProfilesRepository profilesRepository,
                           UsersRepository usersRepository) {
        this.profilesRepository = profilesRepository;
        this.usersRepository = usersRepository;
    }

    /**
     * Crear perfil para un usuario
     */
    @Transactional
    public ProfileResponseDTO createProfile(Integer userId, RegisterPerfileDTO profileDTO) {
        System.out.println("🆕 Creando perfil para usuario ID: " + userId);

        // Verificar que el usuario existe
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + userId));

        // Verificar que no tenga perfil ya
        if (profilesRepository.existsByUser_UserId(userId)) {
            throw new ProfileAlreadyExistsException(userId);
        }

        // Verificar que el documento no esté duplicado
        if (profilesRepository.existsByDocumentNumber(profileDTO.getDocumentNumber())) {
            throw new DuplicateDocumentException(profileDTO.getDocumentNumber());
        }

        // Crear el perfil
        Profiles profile = new Profiles();
        profile.setUser(user);
        profile.setDocumentType(profileDTO.getDocumentType());
        profile.setDocumentNumber(profileDTO.getDocumentNumber());
        profile.setPhoneNumber(profileDTO.getPhoneNumber());
        profile.setBirthDate(profileDTO.getBirthDate());

        Profiles savedProfile = profilesRepository.save(profile);
        System.out.println("✅ Perfil creado exitosamente");

        return mapToResponseDTO(savedProfile);
    }

    /**
     * Actualizar perfil existente
     */
    @Transactional
    public ProfileResponseDTO updateProfile(Integer userId, RegisterPerfileDTO profileDTO) {
        System.out.println("🔄 Actualizando perfil del usuario ID: " + userId);

        Profiles profile = profilesRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new ProfileNotFoundException(userId));

        // Verificar documento duplicado (excepto el propio)
        if (!profile.getDocumentNumber().equals(profileDTO.getDocumentNumber()) &&
                profilesRepository.existsByDocumentNumber(profileDTO.getDocumentNumber())) {
            throw new DuplicateDocumentException(profileDTO.getDocumentNumber());
        }

        // Actualizar campos
        profile.setDocumentType(profileDTO.getDocumentType());
        profile.setDocumentNumber(profileDTO.getDocumentNumber());
        profile.setPhoneNumber(profileDTO.getPhoneNumber());
        profile.setBirthDate(profileDTO.getBirthDate());

        Profiles updatedProfile = profilesRepository.save(profile);
        System.out.println("✅ Perfil actualizado exitosamente");

        return mapToResponseDTO(updatedProfile);
    }

    /**
     * Actualizar información profesional del perfil
     */
    @Transactional
    public void updateProfessionalInfo(
            Integer userId,
            ProfessionalProfileUpdateDTO dto
    ) {
        System.out.println("🧩 Actualizando información profesional del usuario ID: " + userId);

        Profiles profile = profilesRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new ProfileNotFoundException(userId));

        profile.setProfessionalSummary(dto.getProfessionalSummary());
        profile.setCareerAchievements(dto.getCareerAchievements());

        profilesRepository.save(profile);

        System.out.println("✅ Información profesional actualizada");
    }

    /**
     * Obtener perfil por ID de usuario
     */
    @Transactional(readOnly = true)
    public ProfileResponseDTO getProfileByUserId(Integer userId) {
        Profiles profile = profilesRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new ProfileNotFoundException(userId));

        return mapToResponseDTO(profile);
    }

    /**
     * Eliminar perfil
     */
    @Transactional
    public void deleteProfile(Integer userId) {
        Profiles profile = profilesRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new ProfileNotFoundException(userId));

        profilesRepository.delete(profile);
        System.out.println("🗑️ Perfil eliminado para usuario ID: " + userId);
    }

    /**
     * Mapear entidad a DTO de respuesta
     */
    private ProfileResponseDTO mapToResponseDTO(Profiles profile) {
        ProfileResponseDTO dto = new ProfileResponseDTO();
        dto.setProfileId(profile.getProfilesId());
        dto.setUserId(profile.getUserId());
        dto.setDocumentType(profile.getDocumentType());
        dto.setDocumentNumber(profile.getDocumentNumber());
        dto.setPhoneNumber(profile.getPhoneNumber());
        dto.setBirthDate(profile.getBirthDate());
        dto.setCreatedAt(profile.getCreatedAt());
        dto.setUpdatedAt(profile.getUpdatedAt());
        return dto;
    }
}