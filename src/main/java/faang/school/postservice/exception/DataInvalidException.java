package faang.school.postservice.exception;

public class DataInvalidException extends RuntimeException{
    public DataInvalidException(String message) {
        super(message);
    }
}
