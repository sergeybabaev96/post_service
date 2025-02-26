package faang.school.postservice.service;

import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.service.s3.S3Service;
import faang.school.postservice.validator.PostValidator;
import faang.school.postservice.validator.ResourceValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ResourceServiseTest {

    @Mock
    private  S3Service s3Service;
    @Mock
    private  ResourceRepository repository;
    @Mock
    private  ResourceValidator resourceValidator;
    @Mock
    private  PostValidator postValidator;
    @InjectMocks
    private ResourseService resourseService;
    @Captor
    private ArgumentCaptor<Resource> resourceArgumentCaptor;

    @Test
    void testAddResourceSavesResource() {
        List<Resource> resources = List.of();
        Post post = Post.builder()
                .id(1L)
                .authorId(1L)
                .resources(resources).build();

        when(s3Service.uploadFile(null)).thenReturn(Resource.builder()
                .name("test").build());

        resourseService.addResource(post, null);

        verify(repository, times(1)).save(resourceArgumentCaptor.capture());
        Resource resource = resourceArgumentCaptor.getValue();
        assertEquals("test", resource.getName());
    }

    @Test
    void testDeleteResource_shouldDeleteResourceSuccessfully() {
        Post post = Post.builder().id(1L).authorId(1L).build();
        Long resourceId = 1L;

        when(repository.findById(resourceId)).thenReturn(Optional.of(Resource.builder().key("key").build()));
        doNothing().when(s3Service).deleteFile("key");

        resourseService.deleteResource(post, resourceId);

        verify(repository).deleteById(resourceId);
        verify(s3Service).deleteFile("key");
    }
}
