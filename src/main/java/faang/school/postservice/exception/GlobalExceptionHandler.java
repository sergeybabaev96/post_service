package faang.school.postservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PostDtoValidationException.class)
    public ResponseEntity<String> handleValidationException(PostDtoValidationException e) {
        log.warn("Post dto validation exception: {}", e.getMessage());
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
