package com.hse.Curriculum.Exception;

import com.hse.Curriculum.Dto.ApiResponseDTO;
import com.hse.Curriculum.Exception.Login.*;
import com.hse.Curriculum.Exception.Profile.*;
import com.hse.Curriculum.Exception.Education.*;
import com.hse.Curriculum.Exception.Users.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja errores de validación (@Valid)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDTO<Map<String, String>>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDTO.<Map<String, String>>builder()
                        .success(false)
                        .message("Errores de validación en los campos")
                        .status(HttpStatus.BAD_REQUEST.value())
                        .data(errors)
                        .build()
                );
    }

    // ==================== EXCEPCIONES DE AUTENTICACIÓN (401) ====================

    /**
     * Maneja credenciales inválidas (login)
     */
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleInvalidCredentials(
            InvalidCredentialsException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponseDTO.error(
                        ex.getMessage(),
                        HttpStatus.UNAUTHORIZED.value()
                ));
    }

    /**
     * Maneja errores de autenticación de Spring Security
     */
    @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleAuthenticationException(
            org.springframework.security.core.AuthenticationException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponseDTO.error(
                        "Error de autenticación: " + ex.getMessage(),
                        HttpStatus.UNAUTHORIZED.value()
                ));
    }

    // ==================== EXCEPCIONES DE ACCESO (403) ====================

    /**
     * Maneja errores de acceso denegado
     */
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleAccessDenied(
            org.springframework.security.access.AccessDeniedException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponseDTO.error(
                        "Acceso denegado: " + ex.getMessage(),
                        HttpStatus.FORBIDDEN.value()
                ));
    }

    // ==================== EXCEPCIONES NO ENCONTRADO (404) ====================

    /**
     * Maneja recursos no encontrados
     */
    @ExceptionHandler({
            UserNotFoundException.class,
            ProfileNotFoundException.class
    })
    public ResponseEntity<ApiResponseDTO<Object>> handleNotFound(
            RuntimeException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponseDTO.error(
                        ex.getMessage(),
                        HttpStatus.NOT_FOUND.value()
                ));
    }

    // ==================== EXCEPCIONES DE CONFLICTO (409) ====================

    /**
     * Maneja recursos duplicados
     */
    @ExceptionHandler({
            DuplicateEmailException.class,
            DuplicateDocumentException.class,
            ProfileAlreadyExistsException.class
    })
    public ResponseEntity<ApiResponseDTO<Object>> handleConflict(
            RuntimeException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponseDTO.error(
                        ex.getMessage(),
                        HttpStatus.CONFLICT.value()
                ));
    }

    // ==================== EXCEPCIONES DE BAD REQUEST (400) ====================

    /**
     * Maneja contraseña actual incorrecta
     */
    @ExceptionHandler(InvalidCurrentPasswordException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleInvalidCurrentPassword(
            InvalidCurrentPasswordException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDTO.error(
                        ex.getMessage(),
                        HttpStatus.BAD_REQUEST.value()
                ));
    }

    /**
     * Maneja contraseña débil
     */
    @ExceptionHandler(WeakPasswordException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleWeakPassword(
            WeakPasswordException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDTO.error(
                        ex.getMessage(),
                        HttpStatus.BAD_REQUEST.value()
                ));
    }

    /**
     * Maneja excepciones de negocio genéricas (BusinessException)
     * Este handler captura todas las BusinessException que no tienen un handler específico
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleBusinessException(
            BusinessException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDTO.error(
                        ex.getMessage(),
                        HttpStatus.BAD_REQUEST.value()
                ));
    }

    // ==================== EXCEPCIONES GENÉRICAS (500) ====================

    /**
     * Maneja errores genéricos de RuntimeException
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleRuntimeException(
            RuntimeException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error(
                        "Error interno del servidor: " + ex.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR.value()
                ));
    }

    /**
     * Maneja cualquier excepción no capturada
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleGenericException(
            Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error(
                        "Error inesperado: " + ex.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR.value()
                ));
    }

    /**
     * Manejo de EducationNotFoundException
     */
    @ExceptionHandler(EducationNotFoundException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleEducationNotFoundException(
            EducationNotFoundException ex,
            WebRequest request) {

        log.warn("Education not found: {} - URI: {}",
                ex.getMessage(),
                ((ServletWebRequest) request).getRequest().getRequestURI());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponseDTO.error(
                        ex.getMessage(),
                        HttpStatus.NOT_FOUND.value()
                ));
    }

    /**
     * Manejo de InvalidEducationDateException
     */
    @ExceptionHandler(InvalidEducationDateException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleInvalidEducationDateException(
            InvalidEducationDateException ex,
            WebRequest request) {

        log.warn("Invalid education date: {} - URI: {}",
                ex.getMessage(),
                ((ServletWebRequest) request).getRequest().getRequestURI());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDTO.error(
                        ex.getMessage(),
                        HttpStatus.BAD_REQUEST.value()
                ));
    }

    /**
     * Manejo de UnauthorizedEducationAccessException
     */
    @ExceptionHandler(UnauthorizedEducationAccessException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleUnauthorizedEducationAccessException(
            UnauthorizedEducationAccessException ex,
            WebRequest request) {

        log.warn("Unauthorized education access: {} - URI: {}",
                ex.getMessage(),
                ((ServletWebRequest) request).getRequest().getRequestURI());

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponseDTO.error(
                        ex.getMessage(),
                        HttpStatus.FORBIDDEN.value()
                ));
    }
}