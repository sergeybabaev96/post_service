package faang.school.postservice.model.event;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@RedisHash(value = "user")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserEvent {
    @Id
    private Long id;
    private String username;
    private String email;
    private String phone;

    @TimeToLive
    @Value("${spring.data.redis.cache.ttl}")
    private long ttl;
}
