package com.example.Street_manager.exception;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@Builder
public class ResponseException extends RuntimeException {
    private final String message;
    private final String systemMessage;
}
