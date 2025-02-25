package faang.school.postservice.client;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @GetMapping("/liked_post/{id}")
    public List<UserDto> getAllUserWhoLikedPost(@PathVariable("id") Long idPost){
        return likeService.getAllUserWhoLikedPost(idPost);
    }

    @GetMapping("/liked_comment/{id}")
    public List<UserDto> getAllUserWhoLikedComment(@PathVariable("id") Long idComment){
        return likeService.getAllUserWhoLikedComment(idComment);
    }

}
