package com.thespace.thespace.dto.community;

import jakarta.validation.constraints.NotNull;

public record CommunityModifyDTO(@NotNull
                                 Long communityId,
                                 @NotNull
                                 String communityName,
                                 String description) {
}
