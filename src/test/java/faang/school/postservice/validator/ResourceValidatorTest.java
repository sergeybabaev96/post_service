package faang.school.postservice.validator;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class ResourceValidatorTest {

    private ResourceValidator resourceValidator = new ResourceValidator();

    @Test
    void validateResourceLimit() {
        assertThrows(DataValidationException.class, () -> {
            resourceValidator.validateResourceLimit(11);
        });
    }

    @Test
    void validateResourceBelongsToPost() {
        Post post = Post.builder().id(2L).build();
        Resource resource = Resource.builder().post(post).build();
        assertThrows(DataValidationException.class, () -> {
            resourceValidator.validateResourceBelongsToPost(resource, 1L);
        });
    }

}
