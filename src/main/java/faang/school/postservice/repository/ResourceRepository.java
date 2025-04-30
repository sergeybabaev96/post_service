package faang.school.postservice.repository;

import faang.school.postservice.model.Resource;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ResourceRepository extends CrudRepository<Resource, Long> {

    Optional<Resource> findResourceById(long resourceId);
}
