package faang.school.postservice.validator;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class ResourceValidatorTest {

    @InjectMocks
    private ResourceValidator resourceValidator;

    @Test
    void validateResourceLimit() {
        DataValidationException dataValidationException = assertThrows(DataValidationException.class, () -> {
            resourceValidator.validateResourceLimit(11);
        });
        assertEquals("Cannot upload more than 0 images", dataValidationException.getMessage());
    }

    @Test
    void validateResourceBelongsToPost() {
        Post post = Post.builder().id(2L).build();
        Resource resource = Resource.builder().post(post).build();
        DataValidationException dataValidationException = assertThrows(DataValidationException.class, () -> {
            resourceValidator.validateResourceBelongsToPost(resource, 1L);
        });
        assertEquals("Resource does not belong to post", dataValidationException.getMessage());
    }

}
