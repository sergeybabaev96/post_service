package faang.school.postservice.exception;

public class AIIntegrationException extends RuntimeException {
    public AIIntegrationException(String message) {
        super(message);
    }

    public AIIntegrationException(String message, Exception e) {
        super(message, e);
    }
}
