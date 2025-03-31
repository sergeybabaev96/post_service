package faang.school.postservice.validator.creatpost;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CreatorValidator.class)
@Documented
public @interface AuthorOrProjectRequired {
    String message() default "Either authorId or projectId must be provided";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
