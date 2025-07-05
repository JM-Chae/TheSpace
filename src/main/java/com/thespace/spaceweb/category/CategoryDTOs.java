package com.thespace.spaceweb.category;

import com.thespace.spaceweb.category.CategoryDTOs.Create;
import com.thespace.spaceweb.category.CategoryDTOs.Info;
import com.thespace.spaceweb.category.CategoryDTOs.List;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Builder;

sealed public interface CategoryDTOs permits Info, Create, List {

    record Create(@NotNull
                  @Size(min = 1, max = 30)
                  String name,
                  String type,
                  @NotNull
                  Long communityId) implements CategoryDTOs {

    }

    @Builder
    record Info(Long id,
                String name,
                String type,
                LocalDateTime createDate,
                LocalDateTime modDate,
                Long communityId) implements CategoryDTOs {

    }

    record List(Long categoryId,
                String categoryName,
                String categoryType,
                Long communityId) implements CategoryDTOs {
    }
}
