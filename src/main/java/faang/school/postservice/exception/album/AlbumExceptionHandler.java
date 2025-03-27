package faang.school.postservice.exception.album;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AlbumExceptionHandler {

    @ExceptionHandler(AlbumException.class)
    public ResponseEntity<?> catchException(AlbumException e) {
        return ResponseEntity.status(e.getExceptionCode()).body(e.getMessage());
    }
}
