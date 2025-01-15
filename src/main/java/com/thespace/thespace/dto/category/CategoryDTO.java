package com.thespace.thespace.dto.category;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record CategoryDTO(Long categoryId,
                          String categoryName,
                          String categoryType,
                          String path,
                          LocalDateTime createDate,
                          LocalDateTime modDate,
                          Long communityId) {

}
