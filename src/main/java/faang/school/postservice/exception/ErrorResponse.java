package faang.school.postservice.exception;

import lombok.Builder;

@Builder
public class ErrorResponse {
    private Exception exception;
    private String message;
    private String service;
}
