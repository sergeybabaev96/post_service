package faang.school.postservice.exception_handlers;

import faang.school.postservice.exception.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Object> handleDataAccessExceptions(MethodArgumentNotValidException ex) {
        return ResponseEntity.internalServerError().body("Data access error: %s".formatted(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleExceptions(MethodArgumentNotValidException ex) {
        return ResponseEntity.internalServerError().body("Unknown server error: %s".formatted(ex.getMessage()));
    }
}
