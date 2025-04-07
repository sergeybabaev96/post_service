package faang.school.postservice.utils;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.exception.DataValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static faang.school.postservice.utils.ValidationUtils.validateLike;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class ValidationUtilsTest {

    @Test
    public void testValidateLikeLikeDtoNull() {
        LikeDto likeDto = null;
        Exception exception = assertThrows(DataValidationException.class, () -> validateLike(likeDto));
        assertEquals("Invalid data in LikeDto", exception.getMessage());
    }

    @Test
    public void testValidateLikeUserIdNull() {
        LikeDto likeDto = new LikeDto(null, 1L, 2L);
        Exception exception = assertThrows(DataValidationException.class, () -> validateLike(likeDto));
        assertEquals("Invalid data in LikeDto", exception.getMessage());
    }

    @Test
    public void testValidateLikePostIdAndCommentIdNull() {
        LikeDto likeDto = new LikeDto(1L, null, null);
        Exception exception = assertThrows(DataValidationException.class, () -> validateLike(likeDto));
        assertEquals("Invalid data in LikeDto", exception.getMessage());
    }

    @Test
    public void testValidateLikePostIdAndCommentIdNotNull() {
        LikeDto likeDto = new LikeDto(1L, 1L, 2L);
        Exception exception = assertThrows(DataValidationException.class, () -> validateLike(likeDto));
        assertEquals("Invalid data in LikeDto", exception.getMessage());
    }

    @Test
    public void testValidateLikePostIdIsNullAndCommentIdNotNull() {
        LikeDto likeDto = new LikeDto(1L, null, 2L);
        assertDoesNotThrow(() -> validateLike(likeDto));
    }

    @Test
    public void testValidateLikePostIdNotNullAndCommentIdIsNull() {
        LikeDto likeDto = new LikeDto(1L, 1L, null);
        assertDoesNotThrow(() -> validateLike(likeDto));
    }
}
