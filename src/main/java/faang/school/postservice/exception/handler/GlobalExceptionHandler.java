package faang.school.postservice.exception.handler;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFound;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataValidationException.class)
    public ResponseEntity<String> handleDataValidationException(DataValidationException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(EntityNotFound.class)
    public ResponseEntity<String> handleDataNotFoundException(EntityNotFound e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
}