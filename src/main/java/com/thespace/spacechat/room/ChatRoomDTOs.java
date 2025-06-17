package com.thespace.spacechat.room;

import com.thespace.spacechat.room.ChatRoomDTOs.Create;
import com.thespace.spacechat.room.ChatRoomDTOs.Info;
import com.thespace.spacechat.room.ChatRoomDTOs.Invite;
import com.thespace.spacechat.room.ChatRoomDTOs.Summary;
import com.thespace.spacechat.room.ChatRoomDTOs.Update;
import com.thespace.spaceweb.user.UserDTOs;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

sealed public interface ChatRoomDTOs permits Create, Info, Invite, Update, Summary {

    record Info(Long roomId, String name, UserDTOs.Info manager, String description, List<UserDTOs.Info> members, LocalDateTime createdAt, LocalDateTime modifiedAt) implements ChatRoomDTOs {
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

    record Summary(Long rid, String name, String lastSentMessage, LocalDateTime lastSentAt) implements ChatRoomDTOs {
    }
}
