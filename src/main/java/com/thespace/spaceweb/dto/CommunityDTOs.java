package com.thespace.spaceweb.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

sealed public interface CommunityDTOs {

    record Create(@NotNull
                                     String communityName,
                  String description) implements CommunityDTOs {

    }

    @Builder
    record Info(Long communityId,
                String communityName,
                LocalDateTime createDate,
                LocalDateTime modDate,
                String description,
                List<CategoryDTOs.Info> category) implements CommunityDTOs {

    }

    record Modify(@NotNull
                                     Long communityId,
                  @NotNull
                                     String communityName,
                  String description) implements CommunityDTOs {
    }
}
