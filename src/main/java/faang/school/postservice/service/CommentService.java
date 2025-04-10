package faang.school.postservice.service;

import faang.school.postservice.model.Comment;

public interface CommentService {

    Comment findCommentById(Long commentId);
    void moderateComments();
}
