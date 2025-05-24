package com.thespace.thespace.dto.user;

import java.util.List;
import lombok.Builder;


@Builder
public record UserInfoDTO(String uuid,
                          String name,
                          List<String> roles) {
}
