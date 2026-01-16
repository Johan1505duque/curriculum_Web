package com.hse.Curriculum.Dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder

public class ApiResponseDTO<T> {
    private boolean success;
    private String message;
    private int status;
    private T data;

  // Constructor

    public ApiResponseDTO (){}

    public static <T> ApiResponseDTO<T> success(String message, int status, T data) {
        return ApiResponseDTO.<T>builder()
                .success(true)
                .message(message)
                .status(status)
                .data(data)
                .build();
    }

    public ApiResponseDTO(boolean success, String message, int status, T data) {
        this.success = success;
        this.message = message;
        this.status = status;
        this.data = data;
    }

    // Respuesta de error
    public static <T> ApiResponseDTO<T> error(String message, int status) {
        return ApiResponseDTO.<T>builder()
                .success(false)
                .message(message)
                .status(status)
                .data(null)
                .build();
    }

}
