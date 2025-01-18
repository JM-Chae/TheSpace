package com.thespace.thespace.dto.like;

import lombok.Builder;

@Builder
public record LikeDTO(String userId,
                      Long bno,
                      Long rno) {

}
