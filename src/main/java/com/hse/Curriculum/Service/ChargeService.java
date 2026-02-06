package com.hse.Curriculum.Service;
import com.hse.Curriculum.Exception.Post.ChargeNotFoundException;
import com.hse.Curriculum.Exception.Post.DuplicateChargeNameException;
import com.hse.Curriculum.Repository.ChargeRepository;
import com.hse.Curriculum.Models.Charge;
import com.hse.Curriculum.Dto.ChargeDTO.ChargeRegisterDTO;
import com.hse.Curriculum.Dto.ChargeDTO.ChargeResponseDTO;
import com.hse.Curriculum.Dto.ChargeDTO.ChargeUpdateDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChargeService {
    private final ChargeRepository chargeRepository;

    /**
     * Registrar nuevo cargo
     */
    @Transactional
    public Charge register(ChargeRegisterDTO registerDTO, Integer createdBy) {
        System.out.println("📝 Registrando nuevo cargo: " + registerDTO.getNameCharge());

        // Validar que el nombre no esté duplicado
        if (chargeRepository.existsByNameCharge(registerDTO.getNameCharge())) {
            throw new DuplicateChargeNameException(registerDTO.getNameCharge());
        }

        Charge post = new Charge();
        post.setNameCharge(registerDTO.getNameCharge());
        post.setDescription(registerDTO.getDescription());
        post.setStatus(true);
        post.setCreatedBy(createdBy);
        post.setUpdatedBy(createdBy);

        Charge savedPost = chargeRepository.save(post);
        System.out.println("✅ Cargo registrado con ID: " + savedPost.getChargeId());

        return savedPost;
    }

    /**
     * Buscar cargo por ID
     */
    public Optional<Charge> findById(Integer chargeId) {
        System.out.println("🔍 Buscando cargo con ID: " + chargeId);
        Optional<Charge> post = chargeRepository.findById(chargeId);

        if (post.isPresent()) {
            System.out.println("✅ Cargo encontrado: " + post.get().getNameCharge());
        } else {
            System.out.println("⚠️ Cargo con ID " + chargeId + " no existe");
        }

        return post;
    }

    /**
     * Obtener cargo por ID (lanza excepción si no existe)
     */
    public Charge getById(Integer chargeId) {
        return chargeRepository.findById(chargeId)
                .orElseThrow(() -> new ChargeNotFoundException(chargeId));
    }

    /**
     * Buscar cargo por nombre
     */
    public Optional<Charge> findByName(String nameCharge) {
        System.out.println("🔍 Buscando cargo: " + nameCharge);
        return chargeRepository.findByNameCharge(nameCharge);
    }

    /**
     * Listar todos los cargos activos
     */
    public List<ChargeResponseDTO> getAllActive() {
        System.out.println("📋 Listando cargos activos");
        return chargeRepository.findByStatusTrue().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Listar todos los cargos (activos e inactivos)
     */
    public List<ChargeResponseDTO> getAll() {
        System.out.println("📋 Listando todos los cargos");
        return chargeRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Actualizar cargo
     */
    @Transactional
    public Charge update(Integer chargeId, ChargeUpdateDTO updateDTO, Integer updatedBy) {
        System.out.println("🔄 Actualizando cargo ID: " + chargeId);

        Charge charge = chargeRepository.findById(chargeId)
                .orElseThrow(() -> new ChargeNotFoundException(chargeId));

        // Validar nombre duplicado (excluyendo el cargo actual)
        if (updateDTO.getNameCharge() != null &&
                !updateDTO.getNameCharge().equals(charge.getNameCharge()) &&
                chargeRepository.existsByNameChargeAndChargeIdNot(updateDTO.getNameCharge(), chargeId)) {
            throw new DuplicateChargeNameException(updateDTO.getNameCharge());
        }

        // Actualizar campos
        if (updateDTO.getNameCharge() != null) {
            charge.setNameCharge(updateDTO.getNameCharge());
        }
        if (updateDTO.getDescription() != null) {
            charge.setDescription(updateDTO.getDescription());
        }

        charge.setUpdatedBy(updatedBy);

        Charge updatedCharge = chargeRepository.save(charge);
        System.out.println("✅ Cargo actualizado exitosamente");

        return updatedCharge;
    }

    /**
     * Deshabilitar cargo (soft delete)
     */
    @Transactional
    public void disable(Integer chargeId, Integer updatedBy) {
        System.out.println("🔄 Deshabilitando cargo ID: " + chargeId);

        Charge charge = chargeRepository.findById(chargeId)
                .orElseThrow(() -> new ChargeNotFoundException(chargeId));

        charge.setStatus(false);
        charge.setUpdatedBy(updatedBy);
        chargeRepository.save(charge);

        System.out.println("✅ Cargo deshabilitado exitosamente");
    }

    /**
     * Habilitar cargo
     */
    @Transactional
    public void enable(Integer chargeId, Integer updatedBy) {
        System.out.println("🔄 Habilitando cargo ID: " + chargeId);

        Charge charge = chargeRepository.findById(chargeId)
                .orElseThrow(() -> new ChargeNotFoundException(chargeId));

        charge.setStatus(true);
        charge.setUpdatedBy(updatedBy);
        chargeRepository.save(charge);

        System.out.println("✅ Cargo habilitado exitosamente");
    }

    /**
     * Verificar si existe cargo por ID
     */
    public boolean existsById(Integer chargeId) {
        return chargeRepository.existsById(chargeId);
    }

    /**
     * Verificar si existe cargo por nombre
     */
    public boolean existsByName(String nameCharge) {
        return chargeRepository.existsByNameCharge(nameCharge);
    }

    /**
     * Buscar cargos por nombre (búsqueda parcial)
     */
    public List<ChargeResponseDTO> searchByName(String namePost) {
        System.out.println("🔍 Buscando cargos que contengan: " + namePost);
        return chargeRepository.findByNameChargeContainingIgnoreCase(namePost).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convertir entidad a DTO de respuesta
     */
    private ChargeResponseDTO convertToResponseDTO(Charge post) {
        ChargeResponseDTO dto = new ChargeResponseDTO();
        dto.setChargeId(post.getChargeId());
        dto.setNameCharge(post.getNameCharge());
        dto.setDescription(post.getDescription());
        dto.setStatus(post.getStatus());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setUpdatedAt(post.getUpdatedAt());
        return dto;
    }
}
