package com.thespace.thespace.dto.category;

public record CategoryListDTO(Long categoryId,
                              String categoryName,
                              String categoryType,
                              Long communityId) {
}
