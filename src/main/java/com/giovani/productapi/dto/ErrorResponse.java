package com.giovani.productapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Detalhes de erro")
public class ErrorResponse {

    @Schema(description = "Data e hora do erro", example = "2025-05-07T20:30:46.291025")
    private LocalDateTime timestamp;

    @Schema(description = "Mensagem de erro (quando não for uma lista de campos)")
    private String message;

    @Schema(description = "Erros de validação agrupados por campo")
    private Map<String, String> errors;
}