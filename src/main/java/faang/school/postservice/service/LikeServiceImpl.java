package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {
    @Value("${user-service.maxNumberUsersInRequest}")
    private int maxNumberUsersInRequest;

    private final LikeRepository likeRepository;
    private final UserServiceClient userServiceClient;

    @Override
    public List<UserDto> findAllUserWhoLikedPost(Long postId) {
        List<Like> likesToPost = likeRepository.findByPostId(postId);
        List<Long> userIds = likesToPost.stream().map(Like::getUserId).toList();
        List<List<Long>> chunksIds = splitUserIds(userIds);
        List<List<UserDto>> userDtos = new ArrayList<>();
        for (List<Long> chunkIds : chunksIds) {
            userDtos.add(userServiceClient.getUsersByIds(chunkIds));
        }
        return userDtos.stream().flatMap(List::stream).collect(Collectors.toList());
    }

    @Override
    public List<UserDto> findAllUserWhoLikedComment(Long commentId) {
        List<Like> likesToPost = likeRepository.findByCommentId(commentId);
        List<Long> userIds = likesToPost.stream().map(Like::getUserId).toList();
        return userServiceClient.getUsersByIds(userIds);
    }

    private List<List<Long>> splitUserIds(List<Long> ids) {
        List<List<Long>> chunks = IntStream.range(0, (int) Math.ceil((double)ids.size() / maxNumberUsersInRequest))
                .mapToObj(i -> ids.subList(i * maxNumberUsersInRequest,
                        Math.min(ids.size(), (i + 1) * maxNumberUsersInRequest)))
                .toList();
        return chunks;
    }
}
