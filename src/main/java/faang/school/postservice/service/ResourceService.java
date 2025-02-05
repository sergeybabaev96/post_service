package faang.school.postservice.service;

import faang.school.postservice.exception.ResourceNotFoundException;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ResourceService {
    private final ResourceRepository resourceRepository;

    public Resource createResource(Resource resource) {
        return resourceRepository.save(resource);
    }

    public Resource findResourceByKey(String key) {
        return resourceRepository.findByKey(key)
                .orElseThrow(() ->
                        new ResourceNotFoundException("There is no resource with key: " + key));
    }

    public void deleteResource(Resource resource) {
        resourceRepository.delete(resource);
    }
}
