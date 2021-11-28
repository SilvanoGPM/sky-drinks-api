package com.github.skyg0d.skydrinksapi.exception.details;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@SuperBuilder
public class ExceptionDetails {

    protected String title;
    protected String details;
    protected String developerMessage;
    protected int status;
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
