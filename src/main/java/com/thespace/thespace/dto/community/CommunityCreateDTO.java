package com.thespace.thespace.dto.community;

import jakarta.validation.constraints.NotNull;

public record CommunityCreateDTO(@NotNull
                                 String communityName,
                                 String description) {

}
