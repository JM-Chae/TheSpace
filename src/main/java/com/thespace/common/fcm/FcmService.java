
package com.thespace.common.fcm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.thespace.common.exception.Exceptions.FcmTokenNotFound;
import com.thespace.common.notice.NoticeDTO;
import com.thespace.common.notification.Notification;
import com.thespace.common.notification.NotificationDTOs.ToChat_topic;
import com.thespace.spaceweb.user.User;
import com.thespace.spaceweb.user.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class FcmService {

    private final FcmTokenRepository fcmTokenRepository;
    private final UserService userService;
    private final ObjectMapper objectMapper;

    @Transactional
    public void saveToken(User principalUser, String token, String sessionId) {
        String userId = principalUser.getId();
        User user = userService.findById(userId);

        log.info(sessionId);

        fcmTokenRepository.findByToken(token).ifPresentOrElse(
            alreadyToken -> alreadyToken.updateUserAndSessionId(user, sessionId), () -> {
               FcmToken newFcmToken = new FcmToken(user, token, sessionId);
               fcmTokenRepository.save(newFcmToken);
           }
        );
    }

    public void deleteToken(String token) {
        fcmTokenRepository.findByToken(token).ifPresent(fcmTokenRepository::delete);
    }

    @Transactional
    public void deleteTokenBySessionId(String sessionId) {
        List<FcmToken> fcmTokens = fcmTokenRepository.findBySessionId(sessionId);

        if (!fcmTokens.isEmpty()) {
            fcmTokenRepository.deleteAll(fcmTokens);
            log.info("FCM tokens deleted: {}, for session: {}", fcmTokens.size(), sessionId);
        }
    }

    public void sendNotice(NoticeDTO dto) {
        Message message = Message.builder()
            .putData("title", dto.title())
            .putData("body", dto.body())
            .putData("url", dto.url())
            .setTopic(dto.topic().name())
            .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("Successfully sent notice: {}", response);
        } catch (Exception e) {
            log.error("Failed to send notice: {}", e.getMessage());
        }
    }

    public void sendFriendshipRequest(Notification dto) throws JsonProcessingException {
        List<FcmToken> fcmTokens = fcmTokenRepository.findByUser_Id(dto.getUser().getId());
        if(fcmTokens.isEmpty()) throw new FcmTokenNotFound();

        for (FcmToken fcmToken : fcmTokens) {

            Message message = Message.builder()
                .setToken(fcmToken.getToken())
                .putData("nno", String.valueOf(dto.getNno()))
                .putData("fid", String.valueOf(dto.getDataPayload().fid()))
                .putData("title", dto.getTitle())
                .putData("body", dto.getBody())
                .putData("dataPayload", objectMapper.writeValueAsString(dto.getDataPayload()))
                .putData("url", dto.getUrl())
                .putData("type", dto.getType().name())
                .build();

            try {
                String response = FirebaseMessaging.getInstance().send(message);
                log.info("Successfully sent request: {}", response);
            } catch (Exception e) {
                log.error("Failed to send request: {}", e.getMessage());
            }
        }
    }

    public void sendFriendshipAccept(Notification dto) throws JsonProcessingException {
        List<FcmToken> fcmTokens = fcmTokenRepository.findByUser_Id(dto.getUser().getId());
        if(fcmTokens.isEmpty()) throw new FcmTokenNotFound();

        for (FcmToken fcmToken : fcmTokens) {

            Message message = Message.builder()
                .setToken(fcmToken.getToken())
                .putData("nno", String.valueOf(dto.getNno()))
                .putData("title", dto.getTitle())
                .putData("body", dto.getBody())
                .putData("url", dto.getUrl())
                .putData("type", dto.getType().name())
                .putData("dataPayload", objectMapper.writeValueAsString(dto.getDataPayload()))
                .build();

            try {
                String response = FirebaseMessaging.getInstance().send(message);
                log.info("Successfully sent accept: {}", response);
            } catch (Exception e) {
                log.error("Failed to send accept: {}", e.getMessage());
            }
        }
    }

    public void sendChat_topic(ToChat_topic dto) {
        Message message = Message.builder()
            .setTopic(FcmTopic.CHAT + "_room_" + dto.roomId())
            .putData("title", dto.sender())
            .putData("body", dto.content())
            .putData("roomId", dto.roomId().toString())
            .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("Successfully sent message: {}", response);
        } catch (Exception e) {
            log.error("Failed to send message: {}", e.getMessage());
        }
    }
}
