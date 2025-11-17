package com.tradeswift.backend.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private Boolean success;
    private String message;
    private T data;

    @Builder.Default
    private LocalDateTime timeStamp = LocalDateTime.now();

    // static helper
    public static<T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timeStamp(LocalDateTime.now())
                .build();
    }

    public static<T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .timeStamp(LocalDateTime.now())
                .build();
    }
}
