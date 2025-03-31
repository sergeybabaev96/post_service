package faang.school.postservice.validator.creatpost;


import faang.school.postservice.dto.post.CreatePostDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CreatorValidator implements ConstraintValidator<AuthorOrProjectRequired, CreatePostDto> {

    @Override
    public boolean isValid(CreatePostDto dto, ConstraintValidatorContext context) {
        return (dto.getAuthorId() != null) ^ (dto.getProjectId() != null);
    }
}
