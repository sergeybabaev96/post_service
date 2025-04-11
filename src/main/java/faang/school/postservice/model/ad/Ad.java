package faang.school.postservice.model.ad;

import faang.school.postservice.model.Post;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Класс, представляющий сущность "Реклама" в системе.
 * Реклама связана с конкретным постом, имеет ограниченное количество показов
 * и временные рамки активности (дата начала и окончания показа).
 *
 * <p>Реклама может быть активной (если текущая дата в рамках периода показа
 * и остались доступные показы) или неактивной.</p>
 *
 * @author gulnaz21
 */
@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "post_ad")
public class Ad {

    /**
     * Уникальный идентификатор рекламы.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * Пост, связанный с данной рекламой.
     */
    @OneToOne
    @JoinColumn(name = "post_id")
    private Post post;

    /**
     * Идентификатор покупателя, разместившего рекламу.
     */
    @JoinColumn(name = "buyer_id", nullable = false)
    private long buyerId;

    /**
     * Количество оставшихся показов рекламы.
     */
    @Column(name = "appearances_left", nullable = false)
    private long appearancesLeft;

    /**
     * Дата и время начала показа рекламы.
     */
    @Column(name = "start_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime startDate;

    /**
     * Дата и время окончания показа рекламы.
     */
    @Column(name = "end_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime endDate;
}
