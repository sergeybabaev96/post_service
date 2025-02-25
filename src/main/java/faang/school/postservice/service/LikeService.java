package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserServiceClient userServiceClient;
    private final int MAX_NUMBER_ELEMENT = 100;


    public List<UserDto> getAllUserWhoLikedPost(Long idPost){
        List<Like> likedPost = likeRepository.findByPostId(idPost);
        List<Long> idUserLikedPost = likedPost.stream().map(Like::getUserId).toList();

        return getUserWhoLikedContent(idUserLikedPost);
    }

    public List<UserDto> getAllUserWhoLikedComment(Long idComment){
        List<Like> likedComment = likeRepository.findByCommentId(idComment);
        List<Long> idUserLikedComment = likedComment.stream().map(Like::getUserId).toList();

        return getUserWhoLikedContent(idUserLikedComment);
    }

    private List<UserDto> getUserWhoLikedContent(List<Long> listId){
        List<Long> listUserId = listId;
        List<UserDto> userLiked = new ArrayList<>();

        if(listUserId.size() > MAX_NUMBER_ELEMENT){
            int stepCycles = listUserId.size()/MAX_NUMBER_ELEMENT;
            for (int i = 0; i < stepCycles; i++){
                List<Long> groupUserId = listUserId.subList(0, MAX_NUMBER_ELEMENT-1);
                listUserId.subList(0, MAX_NUMBER_ELEMENT-1).clear();
                userLiked.addAll(userServiceClient.getUsersByIds(groupUserId));
            }
            if(!listUserId.isEmpty()) {
                userLiked.addAll(userServiceClient.getUsersByIds(listUserId));
            }
        } else {
            userLiked.addAll(userServiceClient.getUsersByIds(listUserId));
        }
        return userLiked;
    }
}
