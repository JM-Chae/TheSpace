package com.thespace.common.notification;

import static com.thespace.common.notification.DataPayload.forFinalFriendShipStatus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.thespace.common.exception.Exceptions.NotFoundFriendship;
import com.thespace.common.fcm.FcmService;
import com.thespace.common.getList.GetListNotification;
import com.thespace.common.notification.NotificationDTOs.DTO;
import com.thespace.common.notification.NotificationDTOs.ToFriendship_accept;
import com.thespace.common.notification.NotificationDTOs.ToFriendship_request;
import com.thespace.common.page.PageReqDTO;
import com.thespace.common.page.PageResDTO;
import com.thespace.spaceweb.user.User;
import com.thespace.spaceweb.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserService userService;
    private final FcmService fcmService;
    private final GetListNotification getListNotification;

    public void sendNotification(NotificationDTOs dto) {
        try {
            switch (dto) {
                case ToFriendship_request requestDto -> sendFriendshipRequest(requestDto);
                case ToFriendship_accept acceptDto -> sendFriendshipAccept(acceptDto);

                default -> log.warn("Unknown FcmSendDTOs type: {}", dto.getClass().getName());
            }
        } catch (Exception e) {
            log.error("Failed to send FCM notification for DTO type {}: {}", dto.getClass().getName(), e.getMessage(), e);
        }
    }

    public void sendFriendshipRequest(ToFriendship_request dto) throws JsonProcessingException {

        Notification notification = new Notification(userService.getReferenceById(dto.receiverId()),
            "New Friendship Request",
            "You have received a friend request from " + dto.senderNameAndUUID(),
            "/mypage?uuid=" + dto.senderNameAndUUID().split("#")[1],
            DataPayload.forFriendShipRequest(dto.fid(), dto.senderNameAndUUID()),
            NotificationType.FRIENDSHIP_REQUEST
        );

        notificationRepository.save(notification);
        fcmService.sendFriendshipRequest(notification);
        }

    public void sendFriendshipAccept(ToFriendship_accept dto) throws JsonProcessingException {
        Notification notification = new Notification(userService.getReferenceById(dto.receiverId()),
            "New friend!",
            "From now on, you and " + dto.accepterNameAndUUID() + " are friends!",
            "/mypage?uuid=" + dto.accepterNameAndUUID().split("#")[1],
            DataPayload.forFriendShipAccept(dto.accepterNameAndUUID()),
            NotificationType.FRIENDSHIP_ACCEPT
        );

        notificationRepository.save(notification);
        fcmService.sendFriendshipAccept(notification);
    }

    public void updateFriendshipRequestNotification(Long fid, String targetNameAndUUID,FriendshipNotificationStatus status) {
        Notification notification = notificationRepository.findByFriendshipId(fid).orElseThrow(NotFoundFriendship::new);

        if (status == FriendshipNotificationStatus.CANCELLED) {
            notificationRepository.delete(notification);
            return;
        }

        notification.updateDataPayload(forFinalFriendShipStatus(targetNameAndUUID, status));
        notificationRepository.save(notification);
    }

    public PageResDTO<DTO> getNotifications(User user, /*Boolean isRead,*/ PageReqDTO pageReqDTO) {

        Pageable pageable = pageReqDTO.getPageable("nno");

        Page<DTO> list = getListNotification.getList(user, /*isRead,*/ pageable);

        return PageResDTO.from(pageReqDTO, list);
    }
}

