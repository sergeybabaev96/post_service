package faang.school.postservice.controller.handler;

import faang.school.postservice.dto.error.ErrorModel;
import faang.school.postservice.exception.AlbumAccessDeniedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @Value("${service.name}")
    private String serviceName;

    @ExceptionHandler(AlbumAccessDeniedException.class)
    public ErrorModel handleAccessDeniedException(AlbumAccessDeniedException ex) {
        log.error("Album access denied exception", ex);
        return createError("Album access denied exception", HttpStatus.FORBIDDEN.value());
    }

    @ExceptionHandler(Exception.class)
    public ErrorModel handleException(Exception ex) {
        log.error("Internal server error exception", ex);
        return createError("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    private ErrorModel createError(String message, int statusCode) {
        return new ErrorModel(message, statusCode, serviceName);
    }
}
