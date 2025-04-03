package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {

        // Преобразование из CommentCreateDto в Comment
        @Mapping(target = "post", source = "post") // Post передается отдельно
        @Mapping(target = "id", ignore = true) // ID игнорируется при создании
        @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())") // Устанавливаем текущее время
        Comment commentCreateDtoToComment(CommentCreateDto dto, @Context Post post);

        // Преобразование из CommentUpdateDto в Comment
        @Mapping(target = "post", ignore = true) // При обновлении пост не меняется
        @Mapping(target = "authorId", ignore = true) // Автор не меняется
        @Mapping(target = "createdAt", ignore = true) // Время создания не меняется
        Comment commentUpdateDtoToComment(CommentUpdateDto dto);

        // Преобразование из Comment в CommentDto для вывода
        @Mapping(target = "postId", source = "post.id") // Получаем ID поста
        CommentDto commentToDto(Comment comment);
}