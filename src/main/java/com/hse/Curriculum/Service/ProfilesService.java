package com.hse.Curriculum.Service;

import com.hse.Curriculum.Dto.ProfileDTO.PerfileRegisterDTO;
import com.hse.Curriculum.Dto.ProfileDTO.ProfessionalProfileUpdateDTO;
import com.hse.Curriculum.Dto.ProfileDTO.ProfileResponseDTO;
import com.hse.Curriculum.Dto.ProfileDTO.UserProfileDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.hse.Curriculum.Exception.Profile.*;
import com.hse.Curriculum.Models.Profiles;
import com.hse.Curriculum.Models.Users;
import com.hse.Curriculum.Repository.ProfilesRepository;
import com.hse.Curriculum.Repository.UsersRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Slf4j
@Service
public class ProfilesService {

    private final ProfilesRepository profilesRepository;
    private final UsersRepository usersRepository;

    public ProfilesService(ProfilesRepository profilesRepository,
                           UsersRepository usersRepository) {
        this.profilesRepository = profilesRepository;
        this.usersRepository = usersRepository;
    }

    @Transactional
    public Profiles createEmptyProfileForUser(Integer userId) {
        log.info("📝 Creando perfil vacío para usuario ID: {}", userId);

        // Verificar que el usuario existe
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + userId));

        // Verificar que no tenga ya un perfil
        if (profilesRepository.existsByUser_UserId(userId)) {
            log.warn("⚠️ El usuario {} ya tiene un perfil", userId);
            return profilesRepository.findByUser_UserId(userId)
                    .orElseThrow(() -> new RuntimeException("Error al obtener perfil existente"));
        }

        // Crear nuevo perfil vacío con los campos de tu tabla
        Profiles profile = new Profiles();
        profile.setUser(user);

        // Valores por defecto (todos null excepto profileComplete)
        profile.setDocumentType(null);
        profile.setDocumentNumber(null);
        profile.setPhoneNumber(null);
        profile.setProfessionalSummary(null);
        profile.setCareerAchievements(null);
        profile.setBirthDate(null);
        profile.setResidentialAddress(null);
        profile.setProfileComplete(false); // ⭐ Marcar como incompleto

        // Guardar
        Profiles savedProfile = profilesRepository.save(profile);

        log.info("✅ Perfil vacío creado con ID: {} para usuario: {}",
                savedProfile.getProfilesId(), userId);

        return savedProfile;
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
     * Eliminar perfil
     */

    @Transactional
    public void deleteProfile(Integer userId) {
        Profiles profile = profilesRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new ProfileNotFoundException(userId));

        profilesRepository.delete(profile);
        System.out.println("🗑️ Perfil eliminado para usuario ID: " + userId);
    }

    public Profiles getProfileByUserId(Integer userId) {
        return profilesRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new ProfileNotFoundException(
                        "Perfil no encontrado para el usuario: " + userId
                ));
    }

}