package faang.school.postservice.repository.adapter;

import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.ResourceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResourceRepositoryAdapter {

    private final ResourceRepository resourceRepository;

    public Resource findResourceById(long resourceId) {
        return resourceRepository.findResourceById(resourceId)
                .orElseThrow(() -> new EntityNotFoundException("Resource not found"));
    }
}
