package com.thespace.spaceweb.friendship;

import com.thespace.spaceweb.friendship.FriendshipDTOs.Info;
import java.time.LocalDateTime;

sealed public interface FriendshipDTOs permits Info {

    record Info (Long fid, String status, String memo, LocalDateTime acceptedAt) implements FriendshipDTOs {
    }
}
