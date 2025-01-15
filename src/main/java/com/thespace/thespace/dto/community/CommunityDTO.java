package com.thespace.thespace.dto.community;

import com.thespace.thespace.dto.category.CategoryDTO;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record CommunityDTO(Long communityId,
                           String communityName,
                           LocalDateTime createDate,
                           LocalDateTime modDate,
                           String description,
                           List<CategoryDTO> category) {

}
