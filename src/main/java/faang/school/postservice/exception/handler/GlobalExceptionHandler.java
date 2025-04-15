package faang.school.postservice.exception.handler;

import faang.school.postservice.exception.ErrorResponse;
import faang.school.postservice.exception.InvalidFileException;
import faang.school.postservice.exception.MaxResourcesReachedException;
import faang.school.postservice.exception.PostIdMismatchException;
import faang.school.postservice.exception.minio_exceptions.BucketNotFoundException;
import faang.school.postservice.exception.minio_exceptions.MinioRemovingFileException;
import faang.school.postservice.exception.minio_exceptions.MinioUploadingFileException;
import faang.school.postservice.exception.not_found_exceptions.PostNotFoundException;
import faang.school.postservice.exception.not_found_exceptions.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PostNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handlePostNotFoundException(PostNotFoundException e) {
        return ErrorResponse.builder()
                .message(e.getMessage())
                .origin("PostResourceService")
                .errorCode("POST_NOT_FOUND")
                .timestamp(Instant.now())
                .build();
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleResourceNotFoundException(ResourceNotFoundException e) {
        return ErrorResponse.builder()
                .message(e.getMessage())
                .origin("PostResourceService")
                .errorCode("RESOURCE_NOT_FOUND")
                .timestamp(Instant.now())
                .build();
    }

    @ExceptionHandler(PostIdMismatchException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleResourcePostIdNotEqualsPostIdException(PostIdMismatchException e) {
        return ErrorResponse.builder()
                .message(e.getMessage())
                .origin("PostResourceService")
                .errorCode("POST_ID_MISMATCH")
                .timestamp(Instant.now())
                .build();
    }

    @ExceptionHandler(MaxResourcesReachedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMaxResourcesReachedException(MaxResourcesReachedException e) {
        return ErrorResponse.builder()
                .message(e.getMessage())
                .origin("PostResourceService")
                .errorCode("MAX_RESOURCES_REACHED")
                .timestamp(Instant.now())
                .build();
    }

    @ExceptionHandler(InvalidFileException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidFileException(InvalidFileException e) {
        return ErrorResponse.builder()
                .message(e.getMessage())
                .origin("PostResourceService")
                .errorCode("INVALID_FILE")
                .timestamp(Instant.now())
                .build();
    }

    @ExceptionHandler(BucketNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleBucketNotFoundException(BucketNotFoundException e) {
        return ErrorResponse.builder()
                .message(e.getMessage())
                .origin("MinioService")
                .errorCode("BUCKET_NOT_FOUND")
                .timestamp(Instant.now())
                .build();
    }

    @ExceptionHandler(MinioRemovingFileException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMinioRemovingFileException(MinioRemovingFileException e) {
        return ErrorResponse.builder()
                .message(e.getMessage())
                .origin("MinioService")
                .errorCode("MINIO_REMOVING_FILE")
                .timestamp(Instant.now())
                .build();
    }

    @ExceptionHandler(MinioUploadingFileException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMinioUploadingFileException(MinioUploadingFileException e) {
        return ErrorResponse.builder()
                .message(e.getMessage())
                .origin("MinioService")
                .errorCode("MINIO_UPLOADING_FILE")
                .timestamp(Instant.now())
                .build();
    }
}
