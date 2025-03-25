package faang.school.postservice.controller;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import faang.school.postservice.exception.DataValidationException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataValidationException.class)
    public ResponseEntity<Object> handleDataValidationException(DataValidationException ex) {
        return BadRequest(ex);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex) {
        return NotFound(ex);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntimeException(RuntimeException ex) {
        return InternalServerError(ex);
    }

    private ResponseEntity<Object> InternalServerError(Exception ex) {
        return getResponse(ex, 500);
    }

    private ResponseEntity<Object> BadRequest(Exception ex) {
        return getResponse(ex, 400);
    }

    private ResponseEntity<Object> NotFound(Exception ex) {
        return getResponse(ex, 404);
    }

    private ResponseEntity<Object> getResponse(Exception ex, int code) {
        return ResponseEntity
                .status(HttpStatus.valueOf(code))
                .body(ex.getMessage());
    }
}
