package faang.school.postservice.mapper;

import faang.school.postservice.dto.AdDto;
import faang.school.postservice.model.ad.Ad;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AdMapper {


    @Mapping(target = "postId", source = "post.id")
    AdDto toDto(Ad entity);


   default List<Long> extractIdsFromAds(List<Ad> adList) {
        return adList != null
                ? adList.stream().map(Ad::getId).toList()
                : Collections.emptyList();
    }
}
