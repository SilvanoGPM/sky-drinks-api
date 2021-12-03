package com.github.skyg0d.skydrinksapi.exception.details;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@SuperBuilder
public class ExceptionDetails {

    @Schema(description = "Título do exceção", example = "Aconteceu um erro no servidor")
    protected String title;

    @Schema(description = "Detalhes do exceção", example = "java.lang.NullPointerException")
    protected String details;

    @Schema(description = "Mensagem do desenvolvedor no exceção", example = "Por favor, entre em contato com o programador")
    protected String developerMessage;

    @Schema(description = "Status do exceção", example = "500")
    protected int status;

    @Schema(description = "Tempo em que a exceção aconteceu", example = "2021-11-07T18:54:16.167156")
    protected String timestamp;

    public static ExceptionDetails createExceptionDetails(Exception ex, HttpStatus status) {
        return createExceptionDetails(ex, status, "Ocorreu um erro no servidor.");
    }

    public static ExceptionDetails createExceptionDetails(Exception ex, HttpStatus status, String exTitle) {
        Throwable cause = ex.getCause();

        String title = cause != null
                ? cause.getMessage()
                : exTitle;

        return ExceptionDetails
                .builder()
                .status(status.value())
                .title(title)
                .details(ex.getMessage())
                .developerMessage(ex.getClass().getName())
                .timestamp(LocalDateTime.now().toString())
                .build();
    }

}
