package faang.school.postservice.repository;

import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface LikeRepository extends CrudRepository<Like, Long> {

    @Query("SELECT l FROM Like l WHERE l.post = :postId")
    List<Like> findByPostId(long postId);

    @Query("SELECT l FROM Like l WHERE l.comment = :commentId")
    List<Like> findByCommentId(long commentId);
}
