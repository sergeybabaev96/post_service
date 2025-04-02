package faang.school.postservice.repository;

import faang.school.postservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query(nativeQuery = true, value = """
                    SELECT s.follower_id
                    FROM subscription s
                    WHERE s.followee_id = ?1
            """)
    Set<Long> findAllSubscribersById(Long id);
}