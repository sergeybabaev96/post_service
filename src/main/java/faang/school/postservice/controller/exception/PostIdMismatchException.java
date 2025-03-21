package faang.school.postservice.controller.exception;

public class PostIdMismatchException extends RuntimeException {
    public PostIdMismatchException(String message) {
        super(message);
    }
}
