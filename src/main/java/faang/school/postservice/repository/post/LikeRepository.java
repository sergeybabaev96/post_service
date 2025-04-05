package faang.school.postservice.repository.post;

import faang.school.postservice.model.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikeRepository extends JpaRepository<Like, Long> {

    List<Like> findByPostId(long postId);

}