package com.thespace.thespace.dto.category;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CategoryCreateDTO(@NotNull
                                @Size(min = 1, max = 30)
                                String categoryName,
                                String categoryType,
                                String path,
                                @NotNull
                                Long communityId) {

}
