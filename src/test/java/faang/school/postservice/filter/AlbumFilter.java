package faang.school.postservice.filter;

import faang.school.postservice.dto.AlbumFilterDto;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Album_;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlbumPredicateBuilderTest {

    private AlbumFilterDto albumFilterDto = new AlbumFilterDto("test1");
    @Mock
    private CriteriaBuilder criteriaBuilder;
    @Mock
    private Root<Album> albumRoot;
    @Mock
    private Path<String> stringPath;
    @Mock
    private Predicate predicate;

    @Test
    void testGetPredicate_withTitlePattern_returnsLikePredicate() {
        // Arrange
        Root<Album> root = mock(Root.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Path<String> titlePath = mock(Path.class);
        Predicate expectedPredicate = mock(Predicate.class);

        when(root.get(Album_.TITLE)).thenReturn(titlePath);
        when(cb.like(titlePath, "%rock%")).thenReturn(expectedPredicate);

        // Act
        Predicate actualPredicate = new AlbumPredicateBuilder().getPredicate(dto, root, cb);

        // Assert
        assertNotNull(actualPredicate);
        assertEquals(expectedPredicate, actualPredicate);
        verify(cb).like(titlePath, "%rock%");
    }

    // Сценарий: titlePattern == null → возвращается null или пустой предикат
    @Test
    void testGetPredicate_withNullTitlePattern_returnsNull() {
        // Arrange
        AlbumFilterDto dto = mock(AlbumFilterDto.class);
        when(dto.titlePattern()).thenReturn(null);

        Root<Album> root = mock(Root.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        // Act
        Predicate actualPredicate = new AlbumPredicateBuilder().getPredicate(dto, root, cb);

        // Assert
        assertNull(actualPredicate); // или можно вернуть cb.conjunction()
    }

    // Внутренний тестируемый класс (пример)
    static class AlbumPredicateBuilder {
        public Predicate getPredicate(AlbumFilterDto dto, Root<Album> root, CriteriaBuilder cb) {
            if (dto.titlePattern() == null) {
                return null; // или cb.conjunction();
            }
            return cb.like(root.get(Album_.TITLE), "%" + dto.titlePattern() + "%");
        }
    }
}

