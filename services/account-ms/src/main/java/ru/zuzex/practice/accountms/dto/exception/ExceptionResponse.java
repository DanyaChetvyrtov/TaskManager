package ru.zuzex.practice.accountms.dto.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionResponse {
    private String message; // TODO add fields for status and timestamp
    private Map<String, String> errors;

    public ExceptionResponse(String message) {
        this.message = message;
    }
}