package com.thespace.spacechat.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thespace.common.DomainNames;
import com.thespace.common.IdGenerator;
import com.thespace.common.exception.Exceptions.NotMember;
import com.thespace.common.exception.Exceptions.RoomNotFound;
import com.thespace.common.fcm.FcmService;
import com.thespace.common.notification.NotificationDTOs.ToChat_topic;
import com.thespace.spacechat.message.MessageDTOs.Text;
import com.thespace.spacechat.room.RoomService;
import com.thespace.spaceweb.user.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessagePub chatPublisher;
    private final SimpMessagingTemplate messagingTemplate;
    private final MessageRepository messageRepository;
    private final IdGenerator idGenerator;
    private final RoomService roomService;
    private final ObjectMapper objectMapper;
    private final FcmService fcmService;

    Long generateMessageId(Long rid) {
        return idGenerator.midGenerate(rid, DomainNames.MESSAGE);
    }

    public void send(Text dto, User sender) {
        if (roomService.existsRoom(dto.roomId())) throw new RoomNotFound();
        if (roomService.existsUserByRoomIdAndUuid(dto.roomId(), sender.getUuid())) throw new NotMember();

        ChatMessage chatMessage = new ChatMessage(
            generateMessageId(dto.roomId()),
            dto.roomId(),
            sender.getUuid(),
            dto.content(),
            LocalDateTime.now(),
            MessageType.TEXT //
        );

        messageRepository.save(chatMessage);
        chatPublisher.publish(chatMessage);

        fcmService.sendChat_topic(new ToChat_topic(
            dto.roomId(),
            sender.getName() + "#" + sender.getUuid(),
            dto.content())
        );
    }

    public void getRecentMessages(Long rid, User user) {
        if (roomService.existsRoom(rid))
            throw new RoomNotFound();
        if (roomService.existsUserByRoomIdAndUuid(rid, user.getUuid()))
            throw new NotMember();

        List<ChatMessage> res = (messageRepository.getRecentMessages(rid, 30)).stream()
            .map(o -> objectMapper.convertValue(o, ChatMessage.class))
            .collect(Collectors.toList());

        messagingTemplate.convertAndSendToUser(user.getId(), "/queue/chat/room/" + rid, res);

        log.info("Successfully sent recent messages - roomId={}, userId={}, messageCount={}", rid, user.getId(), res.size());
    }
}
