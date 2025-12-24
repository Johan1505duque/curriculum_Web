package com.hse.Curriculum.Service;

import com.hse.Curriculum.Dto.ProfileDTO.PerfileRegisterDTO;
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


}