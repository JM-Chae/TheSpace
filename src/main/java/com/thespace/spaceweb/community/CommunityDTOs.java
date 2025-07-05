package com.thespace.spaceweb.community;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Builder;

sealed public interface CommunityDTOs {

    record Create(@NotNull
                                     String communityName,
                  String description) implements CommunityDTOs {

    }

    @Builder
    record Info(Long id,
                String name,
                LocalDateTime createDate,
                LocalDateTime modDate,
                String description) implements CommunityDTOs {

    }

    record Modify(String description) implements CommunityDTOs {
    }
}
