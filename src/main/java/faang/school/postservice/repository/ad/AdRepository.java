package faang.school.postservice.repository.ad;

import faang.school.postservice.model.ad.Ad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface AdRepository extends JpaRepository<Ad, Long> {

    @Query("SELECT a FROM Ad a WHERE a.post.id = ?1")
    Optional<Ad> findByPostId(long postId);

    List<Ad> findAllByBuyerId(long buyerId);

    @Query("SELECT a FROM Ad a WHERE a.end_Date <= :currentTime or a.appearances_left <=0")
    List<Ad> findExpiredAds(LocalDateTime currentTime);

    @Modifying
    @Query("DELETE FROM Ad WHERE a.ad IN :ids")
    void deleteExpiredAds(List<Long> ids);
}
