package com.hse.Curriculum.Exception;


import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import com.hse.Curriculum.Exception.Country.*;
import com.hse.Curriculum.Exception.Education.*;
import com.hse.Curriculum.Exception.Login.*;
import com.hse.Curriculum.Exception.Profile.*;
import com.hse.Curriculum.Exception.Users.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;



@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.put(error.getField(), error.getDefaultMessage())
        );

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Validation Failed");
        body.put("errors", fieldErrors);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(body);
    }
    // === JSON PARSING ERRORS ===
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex) {

        String message = "Invalid request body";

        // Detectar si es un error de enum
        if (ex.getCause() instanceof InvalidFormatException) {
            InvalidFormatException ife = (InvalidFormatException) ex.getCause();

            if (ife.getTargetType() != null && ife.getTargetType().isEnum()) {
                String fieldName = ife.getPath().get(0).getFieldName();
                String invalidValue = ife.getValue().toString();
                Object[] enumValues = ife.getTargetType().getEnumConstants();

                StringBuilder validValues = new StringBuilder();
                for (int i = 0; i < enumValues.length; i++) {
                    validValues.append(enumValues[i]);
                    if (i < enumValues.length - 1) {
                        validValues.append(", ");
                    }
                }

                message = String.format(
                        "Invalid value '%s' for field '%s'. Accepted values are: [%s]",
                        invalidValue, fieldName, validValues.toString()
                );
            } else {
                message = String.format(
                        "Invalid format for field '%s'",
                        ife.getPath().get(0).getFieldName()
                );
            }
        } else if (ex.getCause() instanceof MismatchedInputException) {
            MismatchedInputException mie = (MismatchedInputException) ex.getCause();
            if (!mie.getPath().isEmpty()) {
                message = String.format(
                        "Invalid input for field '%s'",
                        mie.getPath().get(0).getFieldName()
                );
            }
        }

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Bad Request");
        body.put("message", message);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(body);
    }

    // === COUNTRY EXCEPTIONS ===
    @ExceptionHandler(CountryAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleCountryAlreadyExists(
            CountryAlreadyExistsException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(CountryInUseException.class)
    public ResponseEntity<Map<String, Object>> handleCountryInUse(
            CountryInUseException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(CountryNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleCountryNotFound(
            CountryNotFoundException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    // === EDUCATION EXCEPTIONS ===
    @ExceptionHandler(EducationNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleEducationNotFound(
            EducationNotFoundException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidEducationDateException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidEducationDate(
            InvalidEducationDateException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnauthorizedEducationAccessException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorizedEducationAccess(
            UnauthorizedEducationAccessException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    // === LOGIN EXCEPTIONS ===
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, Object>> handleBusinessException(
            BusinessException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateEmail(
            DuplicateEmailException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidCredentials(
            InvalidCredentialsException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InvalidCurrentPasswordException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidCurrentPassword(
            InvalidCurrentPasswordException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(WeakPasswordException.class)
    public ResponseEntity<Map<String, Object>> handleWeakPassword(
            WeakPasswordException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // === PROFILE EXCEPTIONS ===
    @ExceptionHandler(DuplicateDocumentException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateDocument(
            DuplicateDocumentException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ProfileAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleProfileAlreadyExists(
            ProfileAlreadyExistsException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ProfileNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleProfileNotFound(
            ProfileNotFoundException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    // === USER EXCEPTIONS ===
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFound(
            UserNotFoundException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    // === GENERIC EXCEPTION ===
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(
            Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Internal Server Error");
        body.put("message", "An unexpected error occurred");

        // Log the actual exception for debugging
        ex.printStackTrace();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(body);
    }

    // === HELPER METHOD ===
    private ResponseEntity<Map<String, Object>> buildErrorResponse(
            String message, HttpStatus status) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);

        return ResponseEntity.status(status).body(body);
    }

}
