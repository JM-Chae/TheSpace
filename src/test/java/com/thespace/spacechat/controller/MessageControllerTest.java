package com.thespace.spacechat.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thespace.config.DataBaseCleaner;
import com.thespace.spacechat.message.ChatMessage;
import com.thespace.spacechat.message.MessageDTOs.Text;
import com.thespace.spacechat.message.MessageType;
import com.thespace.spacechat.room.Room;
import com.thespace.spacechat.room.RoomRepository;
import com.thespace.spaceweb.user.User;
import com.thespace.spaceweb.user.UserRepository;
import com.thespace.spaceweb.user.UserRole;
import com.thespace.spaceweb.user.UserRoleRepository;
import com.thespace.spaceweb.user.UserRoleService;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class MessageControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DataBaseCleaner dataBaseCleaner;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private WebSocketStompClient stompClient;
    private StompSession stompSession;
    private Long rid;

    @BeforeEach
    public void setUp() throws Exception {
        //DB cleaning and create user.
        dataBaseCleaner.clear();
        User user = userRepository.save(new User(
            "testerUser",
            "testerUUID",
            "tester",
            "test@test.test",
            passwordEncoder.encode("password"),
            new ArrayList<>(),
            "nice to meet you",
            "😊"
        ));
        rid = userRoleRepository.save(new UserRole("ROLE_USER", new ArrayList<>())).getId();
        userRoleService.setRole(user.getId(), rid);

        //Create ChatRoom And join testUser
        Room entity = new Room(
            "testRoom",
            user,
            "Test",
            new HashSet<>(),
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        Room room = roomRepository.saveAndFlush(entity);
        room.addMember(Set.of(user));
        rid = roomRepository.saveAndFlush(room).getRoomId();

        //Config mapper.
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(objectMapper);

        //Get JSESSIONID
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("id", "testerUser");
        params.add("password", "password");

        ResponseEntity<String> response = restTemplate
            .postForEntity("/user/login", new HttpEntity<>(params, headers), String.class);

        List<String> cookie = response.getHeaders().get("Set-Cookie");
        System.out.println("🍪: " + cookie);

        String jsessionId = cookie.stream()
            .filter(c -> c.startsWith("JSESSIONID"))
            .findFirst()
            .orElseThrow()
            .split(";")[0];

        //Config websocket
        this.stompClient = new WebSocketStompClient(
            new SockJsClient(List.of(new WebSocketTransport(new StandardWebSocketClient())))
        );
        stompClient.setMessageConverter(converter);

        WebSocketHttpHeaders wsHeaders = new WebSocketHttpHeaders();
        wsHeaders.add("Cookie", jsessionId);

        String url = "ws://localhost:" + port + "/chat";
        this.stompSession = stompClient
            .connect(url, wsHeaders, new StompSessionHandlerAdapter() {
            })
            .get(1, TimeUnit.SECONDS);
    }

    @Test
    public void sendAndReceive() throws Exception {
        //receive
        BlockingQueue<ChatMessage> receivedMessages = new ArrayBlockingQueue<>(1);

        stompSession.subscribe("/topic/chat/room/" + rid, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return ChatMessage.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                receivedMessages.add((ChatMessage) payload);
            }
        });

        //given
        Text dto = new Text(rid, "Hello!");

        //when
        stompSession.send("/app/chat/send", dto);

        //result
        ChatMessage received = receivedMessages.poll(3, TimeUnit.SECONDS);
        assertNotNull(received);
        assertEquals("Hello!", received.getContent());

        System.out.print(received.getRoomId() + "\n" +
            received.getMessageId() + "\n" +
            received.getContent() + "\n" +
            received.getSender() + "\n" +
            received.getType() + "\n" +
            received.getSentAt() + "\n");
    }

    @Test
    public void getRecent() throws Exception {
        //receive
        BlockingQueue<List<ChatMessage>> receivedMessages = new ArrayBlockingQueue<>(1);

        stompSession.subscribe("/user/queue/chat/room/" + rid, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return new ParameterizedTypeReference<List<ChatMessage>>() {}.getType();
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                List<?> list = (List<?>) payload;
                List<ChatMessage> chatMessages = list.stream()
                    .map(item -> objectMapper.convertValue(item, ChatMessage.class))
                    .collect(Collectors.toList());
                receivedMessages.add(chatMessages);
            }
        });

        //given
        for(long i = 1L; i < 32L; i++) {
            ChatMessage message = new ChatMessage(i, rid, "testerUUID", "test: " + i, LocalDateTime.now(), MessageType.TEXT);
            redisTemplate.opsForList().rightPush("chat:room:" + rid + ":messages", message);
        }

        //when
        stompSession.send("/app/chat/recent", rid);

        //result
        List<ChatMessage> received = receivedMessages.poll(3, TimeUnit.SECONDS);

        received.sort(Comparator.comparing(ChatMessage::getMessageId));

        for (ChatMessage message : received) {
            System.out.printf("roomId: %s messageId: %d senderNameAndUUID: %s content: %s type: %s sentAt: %s%n",
                message.getRoomId(), message.getMessageId(), message.getSender(),
                message.getContent(), message.getType(), message.getSentAt());
            System.out.println("----------------------");
       }
    }
}
