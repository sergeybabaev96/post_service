package faang.school.postservice.exception;

public class EntityNotFound extends RuntimeException {
    public EntityNotFound(String message) {
        super(message);
    }
}
