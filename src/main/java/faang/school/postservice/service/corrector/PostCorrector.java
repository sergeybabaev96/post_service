package faang.school.postservice.service.corrector;

import faang.school.postservice.client.spell.SpellServiceClient;
import faang.school.postservice.dto.spell.SpellDto;
import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PostCorrector {

    private final SpellServiceClient spellServiceClient;

    public void autocorrect(Post post) {
        String originalText = post.getContent();
        List<SpellDto> spellList = spellServiceClient.checkText(originalText);

        if (CollectionUtils.isEmpty(spellList)) {
            return;
        }

        StringBuilder result = new StringBuilder(originalText);

        for (SpellDto spell : spellList) {
            result.replace(spell.getPos(), spell.getPos() + spell.getLen(), spell.getSuggestions().get(0));
        }
        post.setContent(result.toString());
    }
}
