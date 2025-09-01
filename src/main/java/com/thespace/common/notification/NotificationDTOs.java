package com.thespace.common.notification;

import com.thespace.common.notification.NotificationDTOs.DTO;
import com.thespace.common.notification.NotificationDTOs.ToChat_topic;
import com.thespace.common.notification.NotificationDTOs.ToFriendship_accept;
import com.thespace.common.notification.NotificationDTOs.ToFriendship_request;
import com.thespace.common.notification.NotificationDTOs.ToToken;
import java.time.LocalDateTime;

public sealed interface NotificationDTOs permits ToToken, ToChat_topic,
    ToFriendship_request, ToFriendship_accept, DTO {

    record ToToken(String receiverUUID, String title, String body) implements NotificationDTOs {}

    record ToChat_topic(Long roomId, String sender, String content) implements NotificationDTOs {}

    record ToFriendship_request(Long fid, String senderNameAndUUID, String receiverId) implements NotificationDTOs {}

    record ToFriendship_accept(String accepterNameAndUUID, String receiverId) implements NotificationDTOs {}

    record DTO(Long nno, String title, String body, String url, DataPayload dataPayload, NotificationType type, LocalDateTime sentAt) implements NotificationDTOs {
        public static DTO fromEntity(Notification entity) {
            return new DTO(
                entity.getNno(),
                entity.getTitle(),
                entity.getBody(),
                entity.getUrl(),
                entity.getDataPayload(),
                entity.getType(),
                entity.getCreateDate()
            );
        }
    }
}
