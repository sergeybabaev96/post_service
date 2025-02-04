package faang.school.postservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "post_resource")
public class Resource extends BaseEntity {

    @Column(name = "key", nullable = false, length = 50)
    private String key;

    @Column(name = "size")
    private long size;

    @Column(name = "name", length = 150)
    private String name;

    @Column(name = "type", length = 50)
    private String type;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;
}
