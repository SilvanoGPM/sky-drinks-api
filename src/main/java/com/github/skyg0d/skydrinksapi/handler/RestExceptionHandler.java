package com.github.skyg0d.skydrinksapi.handler;

import com.github.skyg0d.skydrinksapi.exception.BadRequestException;
import com.github.skyg0d.skydrinksapi.exception.CustomFileNotFoundException;
import com.github.skyg0d.skydrinksapi.exception.FileStorageException;
import com.github.skyg0d.skydrinksapi.exception.UserCannotModifyClientRequestException;
import com.github.skyg0d.skydrinksapi.exception.details.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

    @ExceptionHandler(CustomFileNotFoundException.class)
    public ResponseEntity<CustomFileNotFoundExceptionDetails> handleCustomFileNotFoundException(
            CustomFileNotFoundException ex
    ) {
        CustomFileNotFoundExceptionDetails exceptionDetails = CustomFileNotFoundExceptionDetails.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .title("Exceção do tipo CustomFileNotFoundException aconteceu, consulte a documentação.")
                .details(ex.getMessage())
                .developerMessage(ex.getClass().getName())
                .timestamp(LocalDateTime.now().toString())
                .build();

        return new ResponseEntity<>(exceptionDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<FileStorageExceptionDetails> handleFileStorageException(FileStorageException ex) {
        FileStorageExceptionDetails exceptionDetails = FileStorageExceptionDetails.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .title("Exceção do tipo FileStorageException aconteceu, consulte a documentação.")
                .details(ex.getMessage())
                .developerMessage(ex.getClass().getName())
                .timestamp(LocalDateTime.now().toString())
                .build();

        return new ResponseEntity<>(exceptionDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<BadRequestExceptionDetails> handleBadRequestException(BadRequestException exception) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        BadRequestExceptionDetails exceptionDetails = BadRequestExceptionDetails
                .builder()
                .title("Exceção do tipo BadRequestException aconteceu, consulte a documentação.")
                .developerMessage(exception.getClass().getName())
                .details(exception.getMessage())
                .timestamp(LocalDateTime.now().toString())
                .status(status.value())
                .build();

        return new ResponseEntity<>(exceptionDetails, status);
    }

    @ExceptionHandler(UserCannotModifyClientRequestException.class)
    public ResponseEntity<UserCannotModifyClientRequestExceptionDetails> handleUserCannotModifyClientRequestException(UserCannotModifyClientRequestException exception) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        UserCannotModifyClientRequestExceptionDetails exceptionDetails = UserCannotModifyClientRequestExceptionDetails
                .builder()
                .title("Este usuário não pode modificar o drink desejado!")
                .developerMessage(exception.getClass().getName())
                .details(exception.getMessage())
                .request(exception.getRequest())
                .triedUser(exception.getTriedUser())
                .timestamp(LocalDateTime.now().toString())
                .status(status.value())
                .build();

        return new ResponseEntity<>(exceptionDetails, status);
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
                .timestamp(LocalDateTime.now().toString())
                .status(status.value())
                .fieldErrors(fieldsErrors)
                .build();

        return new ResponseEntity<>(exceptionDetails, status);
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
