package com.thespace.spaceweb.friendship;

import com.thespace.spaceweb.friendship.FriendshipDTOs.Friend;
import com.thespace.spaceweb.friendship.FriendshipDTOs.Info;
import java.time.LocalDateTime;

sealed public interface FriendshipDTOs permits Info, Friend {

    record Info (Long fid, String status, String note, LocalDateTime acceptedAt) implements FriendshipDTOs {
    }

    record Friend (Long fid, String fsignature, String fname, String fuuid, String note, LocalDateTime acceptedAt) implements FriendshipDTOs {
    }
}
