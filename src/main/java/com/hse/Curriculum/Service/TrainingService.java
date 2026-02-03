package com.hse.Curriculum.Service;
import com.hse.Curriculum.Dto.TrainingDTO.TrainingCreateDTO;
import com.hse.Curriculum.Dto.TrainingDTO.TrainingResponseDTO;
import com.hse.Curriculum.Dto.TrainingDTO.TrainingUpdateDTO;
import com.hse.Curriculum.Exception.Training.InvalidCompletionDateException;
import com.hse.Curriculum.Exception.Training.InvalidTrainingDataException;
import com.hse.Curriculum.Exception.Training.TrainingNotFoundException;
import com.hse.Curriculum.Exception.Training.TrainingUnauthorizedAccessException;
import com.hse.Curriculum.Models.Training;
import com.hse.Curriculum.Models.Users;
import com.hse.Curriculum.Repository.TrainingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio que maneja la lógica de negocio para las capacitaciones
 * Implementa operaciones CRUD y validaciones necesarias
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TrainingService {
    private final TrainingRepository trainingRepository;
    private final UsersService usersService;

    /**
     * Crea un nuevo registro de capacitación
     *
     * @param userId ID del usuario propietario
     * @param dto Datos de la capacitación a crear
     * @return DTO con los datos del registro creado
     * @throws InvalidCompletionDateException Si los datos de finalización son inconsistentes
     * @throws InvalidTrainingDataException Si los datos de duración son inválidos
     */
    @Transactional
    public TrainingResponseDTO createTraining(Integer userId, TrainingCreateDTO dto) {
        log.info("Creando nuevo registro de capacitación para usuario ID: {}", userId);

        // Validar datos de finalización
        validateCompletionData(dto.getIsCompleted(), dto.getCompletionDate());

        // Validar datos de duración
        validateDurationData(dto.getDurationValue(), dto.getDurationUnit());

        // Buscar usuario
        Users user = usersService.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario con ID " + userId + " no encontrado"));

        // Crear entidad Training
        Training training = Training.builder()
                .user(user)
                .courseName(dto.getCourseName().trim())
                .trainingType(dto.getTrainingType().trim())
                .institution(dto.getInstitution().trim())
                .isCompleted(dto.getIsCompleted())
                .durationValue(dto.getDurationValue())
                .durationUnit(dto.getDurationUnit() != null ? dto.getDurationUnit().trim() : null)
                .completionDate(dto.getCompletionDate())
                .build();

        // Guardar en base de datos
        Training savedTraining = trainingRepository.save(training);
        log.info("Registro de capacitación creado exitosamente con ID: {}", savedTraining.getTrainingId());

        // Convertir a DTO de respuesta
        return convertToResponseDTO(savedTraining);
    }

    /**
     * Obtiene todos los registros de capacitación de un usuario
     *
     * @param userId ID del usuario
     * @return Lista de capacitaciones del usuario
     */
    @Transactional(readOnly = true)
    public List<TrainingResponseDTO> getTrainingsByUserId(Integer userId) {
        log.info("Obteniendo capacitaciones del usuario ID: {}", userId);

        // Verificar que el usuario existe
        usersService.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario con ID " + userId + " no encontrado"));

        // Obtener capacitaciones
        List<Training> trainings = trainingRepository.findByUserIdOrderByCompletionDateDesc(userId);

        log.info("Se encontraron {} capacitaciones para el usuario ID: {}", trainings.size(), userId);

        return trainings.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un registro de capacitación por su ID
     * Verifica que el usuario tenga permisos para acceder al registro
     *
     * @param trainingId ID del registro
     * @param userId ID del usuario solicitante
     * @param isAdmin Si el usuario tiene rol de administrador
     * @return DTO con los datos del registro
     * @throws TrainingNotFoundException Si no se encuentra el registro
     * @throws TrainingUnauthorizedAccessException Si el usuario no tiene permisos
     */
    @Transactional(readOnly = true)
    public TrainingResponseDTO getTrainingById(Integer trainingId, Integer userId, boolean isAdmin) {
        log.info("Obteniendo capacitación ID: {} solicitado por usuario ID: {}", trainingId, userId);

        Training training = trainingRepository.findByIdWithUser(trainingId)
                .orElseThrow(() -> new TrainingNotFoundException(trainingId));

        // Verificar autorización: solo el propietario o admin pueden ver
        if (!isAdmin && !training.getUser().getUserId().equals(userId)) {
            log.warn("Usuario ID: {} intentó acceder a capacitación ID: {} sin permisos", userId, trainingId);
            throw new TrainingUnauthorizedAccessException();
        }

        return convertToResponseDTO(training);
    }

    /**
     * Obtiene la entidad Training por su ID (para uso interno)
     *
     * @param trainingId ID del registro
     * @return Entidad Training
     * @throws TrainingNotFoundException Si no se encuentra el registro
     */
    @Transactional(readOnly = true)
    public Training getTrainingEntityById(Integer trainingId) {
        return trainingRepository.findByIdWithUser(trainingId)
                .orElseThrow(() -> new TrainingNotFoundException(trainingId));
    }

    /**
     * Actualiza un registro de capacitación existente
     * Solo actualiza los campos que no son nulos en el DTO
     *
     * @param trainingId ID del registro a actualizar
     * @param userId ID del usuario solicitante
     * @param isAdmin Si el usuario tiene rol de administrador o soporte
     * @param dto Datos a actualizar
     * @return DTO con los datos actualizados
     * @throws TrainingNotFoundException Si no se encuentra el registro
     * @throws TrainingUnauthorizedAccessException Si el usuario no tiene permisos
     */
    @Transactional
    public TrainingResponseDTO updateTraining(Integer trainingId, Integer userId, boolean isAdmin, TrainingUpdateDTO dto) {
        log.info("Actualizando capacitación ID: {} por usuario ID: {} (Admin: {})", trainingId, userId, isAdmin);

        // Buscar registro existente
        Training training = trainingRepository.findByIdWithUser(trainingId)
                .orElseThrow(() -> new TrainingNotFoundException(trainingId));

        // Verificar autorización: propietario, admin o support
        if (!isAdmin && !training.getUser().getUserId().equals(userId)) {
            log.warn("Usuario ID: {} intentó actualizar capacitación ID: {} sin permisos", userId, trainingId);
            throw new TrainingUnauthorizedAccessException("No tiene permisos para actualizar este registro");
        }

        // Actualizar campos si no son nulos
        if (dto.getCourseName() != null) {
            training.setCourseName(dto.getCourseName().trim());
        }

        if (dto.getTrainingType() != null) {
            training.setTrainingType(dto.getTrainingType().trim());
        }

        if (dto.getInstitution() != null) {
            training.setInstitution(dto.getInstitution().trim());
        }

        if (dto.getIsCompleted() != null) {
            training.setIsCompleted(dto.getIsCompleted());
        }

        if (dto.getDurationValue() != null) {
            training.setDurationValue(dto.getDurationValue());
        }

        if (dto.getDurationUnit() != null) {
            training.setDurationUnit(dto.getDurationUnit().trim());
        }

        if (dto.getCompletionDate() != null) {
            training.setCompletionDate(dto.getCompletionDate());
        }

        // Validar datos de finalización después de actualizar
        validateCompletionData(training.getIsCompleted(), training.getCompletionDate());

        // Validar datos de duración después de actualizar
        validateDurationData(training.getDurationValue(), training.getDurationUnit());

        // Guardar cambios
        Training updatedTraining = trainingRepository.save(training);
        log.info("Capacitación ID: {} actualizada exitosamente", trainingId);

        return convertToResponseDTO(updatedTraining);
    }

    /**
     * Elimina un registro de capacitación
     * Solo el propietario o un administrador pueden eliminar
     *
     * @param trainingId ID del registro a eliminar
     * @param userId ID del usuario solicitante
     * @param isAdmin Si el usuario tiene rol de administrador
     * @throws TrainingNotFoundException Si no se encuentra el registro
     * @throws TrainingUnauthorizedAccessException Si el usuario no tiene permisos
     */
    @Transactional
    public void deleteTraining(Integer trainingId, Integer userId, boolean isAdmin) {
        log.info("Eliminando capacitación ID: {} solicitado por usuario ID: {}", trainingId, userId);

        // Buscar registro
        Training training = trainingRepository.findByIdWithUser(trainingId)
                .orElseThrow(() -> new TrainingNotFoundException(trainingId));

        // Verificar autorización: propietario o admin
        if (!isAdmin && !training.getUser().getUserId().equals(userId)) {
            log.warn("Usuario ID: {} intentó eliminar capacitación ID: {} sin permisos", userId, trainingId);
            throw new TrainingUnauthorizedAccessException("No tiene permisos para eliminar este registro");
        }

        // Eliminar registro
        trainingRepository.delete(training);
        log.info("Capacitación ID: {} eliminada exitosamente", trainingId);
    }

    /**
     * Obtiene capacitaciones completadas de un usuario
     *
     * @param userId ID del usuario
     * @return Lista de capacitaciones completadas
     */
    @Transactional(readOnly = true)
    public List<TrainingResponseDTO> getCompletedTrainings(Integer userId) {
        log.info("Obteniendo capacitaciones completadas del usuario ID: {}", userId);

        List<Training> trainings = trainingRepository.findByUserIdAndIsCompleted(userId, true);

        return trainings.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene capacitaciones en curso de un usuario
     *
     * @param userId ID del usuario
     * @return Lista de capacitaciones en curso
     */
    @Transactional(readOnly = true)
    public List<TrainingResponseDTO> getInProgressTrainings(Integer userId) {
        log.info("Obteniendo capacitaciones en curso del usuario ID: {}", userId);

        List<Training> trainings = trainingRepository.findByUserIdAndIsCompleted(userId, false);

        return trainings.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Valida que los datos de finalización sean consistentes
     *
     * @param isCompleted Si el curso está completado
     * @param completionDate Fecha de finalización
     * @throws InvalidCompletionDateException Si los datos son inconsistentes
     */
    private void validateCompletionData(Boolean isCompleted, LocalDate completionDate) {
        if (Boolean.TRUE.equals(isCompleted) && completionDate == null) {
            throw new InvalidCompletionDateException("Los cursos completados deben tener una fecha de finalización");
        }

        if (completionDate != null && completionDate.isAfter(LocalDate.now())) {
            throw new InvalidCompletionDateException("La fecha de finalización no puede ser futura");
        }
    }

    /**
     * Valida que los datos de duración sean consistentes
     *
     * @param durationValue Valor de la duración
     * @param durationUnit Unidad de la duración
     * @throws InvalidTrainingDataException Si los datos son inconsistentes
     */
    private void validateDurationData(Integer durationValue, String durationUnit) {
        // Si se proporciona valor, debe haber unidad y viceversa
        if (durationValue != null && (durationUnit == null || durationUnit.trim().isEmpty())) {
            throw new InvalidTrainingDataException("Debe especificar la unidad de duración cuando proporciona un valor");
        }

        if ((durationUnit != null && !durationUnit.trim().isEmpty()) && durationValue == null) {
            throw new InvalidTrainingDataException("Debe especificar el valor de duración cuando proporciona una unidad");
        }
    }

    /**
     * Convierte una entidad Training a TrainingResponseDTO
     *
     * @param training Entidad a convertir
     * @return DTO de respuesta
     */
    private TrainingResponseDTO convertToResponseDTO(Training training) {
        Users user = training.getUser();

        // Formatear duración
        String durationFormatted = null;
        if (training.getDurationValue() != null && training.getDurationUnit() != null) {
            durationFormatted = training.getDurationValue() + " " + training.getDurationUnit();
        }

        return TrainingResponseDTO.builder()
                .trainingId(training.getTrainingId())
                .userId(user.getUserId())
                .userName(user.getFirstName() + " " + user.getLastName())
                .userEmail(user.getEmail())
                .courseName(training.getCourseName())
                .trainingType(training.getTrainingType())
                .institution(training.getInstitution())
                .isCompleted(training.getIsCompleted())
                .durationValue(training.getDurationValue())
                .durationUnit(training.getDurationUnit())
                .durationFormatted(durationFormatted)
                .completionDate(training.getCompletionDate())
                .createdAt(training.getCreatedAt())
                .updatedAt(training.getUpdatedAt())
                .build();
    }
}
