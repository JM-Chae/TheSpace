package com.thespace.spacechat.dto;

import com.thespace.spacechat.dto.ChatRoomDTOs.Create;
import com.thespace.spacechat.dto.ChatRoomDTOs.Info;
import com.thespace.spacechat.dto.ChatRoomDTOs.Invite;
import com.thespace.spacechat.dto.ChatRoomDTOs.Update;
import com.thespace.spaceweb.dto.UserDTOs;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

sealed public interface ChatRoomDTOs permits Create, Info, Invite, Update {

    record Info(Long roomId, String name, String creatorUuid, String description, List<UserDTOs.Info> members, LocalDateTime createdAt, LocalDateTime modifiedAt) implements ChatRoomDTOs {
    }

    record Create(@NotNull
                  @Size(min = 4, max = 20)
                  String name,
                  @NotNull
                  @Size(min = 4, max = 200)
                  String description,
                  List<String> members) implements ChatRoomDTOs {
    }

    record Update(@NotNull
                  @Size(min = 4, max = 20)
                  String name,
                  @NotNull
                  @Size(min = 4, max = 200)
                  String description) implements ChatRoomDTOs {
    }

    record Invite(@NotNull List<String> members) implements ChatRoomDTOs {
    }

}
