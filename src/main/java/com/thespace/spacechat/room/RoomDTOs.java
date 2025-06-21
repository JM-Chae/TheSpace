package com.thespace.spacechat.room;

import com.thespace.spacechat.room.RoomDTOs.Create;
import com.thespace.spacechat.room.RoomDTOs.Info;
import com.thespace.spacechat.room.RoomDTOs.Invite;
import com.thespace.spacechat.room.RoomDTOs.Summary;
import com.thespace.spacechat.room.RoomDTOs.Update;
import com.thespace.spaceweb.user.UserDTOs;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

sealed public interface RoomDTOs permits Create, Info, Invite, Update, Summary {

    record Info(Long roomId, String name, UserDTOs.Info manager, String description, List<UserDTOs.Info> members, LocalDateTime createdAt, LocalDateTime modifiedAt) implements
        RoomDTOs {
    }

    record Create(@NotNull
                  @Size(min = 4, max = 20)
                  String name,
                  @NotNull
                  @Size(min = 4, max = 200)
                  String description,
                  List<String> members) implements RoomDTOs {
    }

    record Update(@NotNull
                  @Size(min = 4, max = 20)
                  String name,
                  @NotNull
                  @Size(min = 4, max = 200)
                  String description) implements RoomDTOs {
    }

    record Invite(@NotNull List<String> members) implements RoomDTOs {
    }

    record Summary(Long rid, String name, String lastSentMessage, LocalDateTime lastSentAt) implements
        RoomDTOs {
    }
}
