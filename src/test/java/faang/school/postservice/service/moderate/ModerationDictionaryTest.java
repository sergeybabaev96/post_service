package faang.school.postservice.service.moderate;

import faang.school.postservice.config.app.PostServiceConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ModerationDictionaryTest {
    @Mock
    private PostServiceConfiguration postServiceConfiguration;

    @Test
    void containsBadWords() {
    }
}