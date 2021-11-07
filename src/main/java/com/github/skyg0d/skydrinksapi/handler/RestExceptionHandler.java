package com.github.skyg0d.skydrinksapi.handler;

import com.github.skyg0d.skydrinksapi.exception.BadRequestException;
import com.github.skyg0d.skydrinksapi.exception.BadRequestExceptionDetails;
import com.github.skyg0d.skydrinksapi.exception.ExceptionDetails;
import com.github.skyg0d.skydrinksapi.exception.ValidationExceptionDetails;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionDetails> handleException(Exception ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return new ResponseEntity<>(ExceptionDetails.createExceptionDetails(ex, status), status);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return new ResponseEntity<>(ExceptionDetails.createExceptionDetails(ex, status), status);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request
    ) {
        Map<String, List<String>> fieldsErrors = getFormattedFieldsErrors(ex.getBindingResult().getFieldErrors());

        ValidationExceptionDetails exceptionDetails = ValidationExceptionDetails
                .builder()
                .title("Confira os campos do objeto enviado.")
                .developerMessage(ex.getClass().getName())
                .details("Aconteceu um erro de validação em uma das propriedades do objeto, por favor entre com valores corretos")
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .fieldErrors(fieldsErrors)
                .build();

        return new ResponseEntity<>(exceptionDetails, status);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<BadRequestExceptionDetails> handleBadRequestException(BadRequestException exception) {
        BadRequestExceptionDetails exceptionDetails = BadRequestExceptionDetails
                .builder()
                .title("Exceção do tipo BadRequestException aconteceu, consulte a documentação.")
                .developerMessage(exception.getClass().getName())
                .details(exception.getMessage())
                .timestamp(LocalDateTime.now())
                .status(exception.getRawStatusCode())
                .build();

        return new ResponseEntity<>(exceptionDetails, exception.getStatus());
    }

    private Map<String, List<String>> getFormattedFieldsErrors(List<FieldError> fieldErrors) {
        return fieldErrors.stream().collect(Collectors.groupingBy(
                FieldError::getField,
                Collectors.mapping((fieldError -> (Optional
                        .ofNullable(fieldError.getDefaultMessage())
                        .orElse("Aconteceu um erro.")
                )), Collectors.toList())
        ));
    }

}
