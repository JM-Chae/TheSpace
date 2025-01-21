package com.thespace.thespace.dto.user;

import java.util.List;
import lombok.Builder;


@Builder
public record UserInfoDTO(String uuid,
                          String id,
                          String name,
                          String email,
                          List<String> roles) {

}
