package faang.school.postservice.config.context;

import org.springframework.stereotype.Component;

@Component
public class UserContext {

    private final ThreadLocal<Long> userIdHolder = new ThreadLocal<>();

    public void setRequesterId(long requesterId) {
        userIdHolder.set(requesterId);
    }

    public long getRequesterId() {
        return userIdHolder.get();
    }

    public void clear() {
        userIdHolder.remove();
    }
}
