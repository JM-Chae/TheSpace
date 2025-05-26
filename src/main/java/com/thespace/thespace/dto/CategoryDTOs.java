package com.thespace.thespace.dto;

import com.thespace.thespace.dto.CategoryDTOs.Create;
import com.thespace.thespace.dto.CategoryDTOs.Info;
import com.thespace.thespace.dto.CategoryDTOs.List;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Builder;

sealed public interface CategoryDTOs permits Info, Create, List {

    record Create(@NotNull
                                    @Size(min = 1, max = 30)
                                    String categoryName,
                  String categoryType,
                  String path,
                  @NotNull
                                    Long communityId) implements CategoryDTOs {

    }

    @Builder
    record Info(Long categoryId,
                String categoryName,
                String categoryType,
                String path,
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
