package nukem.chatroom.config.socket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nukem.chatroom.dto.UserDto;
import nukem.chatroom.enums.headers.EventType;
import nukem.chatroom.enums.headers.Header;
import nukem.chatroom.model.user.User;
import nukem.chatroom.repository.UserRepository;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.util.Collections;

import static nukem.chatroom.constants.Constants.SLASH;
import static nukem.chatroom.constants.WebSocketURL.CHATROOMS;

@Component
@RequiredArgsConstructor
@Slf4j
public class SocketEventListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;

    @EventListener
    public void handleSessionSubscribeEvent(SessionSubscribeEvent event) {
        final String destination = event.getMessage().getHeaders().get("simpDestination").toString();
        final User user = userRepository.findByUsername(event.getUser().getName()).orElseThrow();
        messagingTemplate.convertAndSend(destination, UserDto.toDto(user),
                Collections.singletonMap(Header.EVENT_TYPE.getValue(), EventType.SUBSCRIBE_EVENT.getValue()));
        log.debug("User {} subscribed to destination: {}", user.getUsername(), destination);
    }

    @EventListener
    public void handleSessionUnsubscribeEvent(SessionUnsubscribeEvent event) {
        final String destination = CHATROOMS + SLASH + event.getMessage().getHeaders().get("simpSubscriptionId").toString();
        final User user = userRepository.findByUsername(event.getUser().getName()).orElseThrow();
        messagingTemplate.convertAndSend(destination, UserDto.toDto(user),
                Collections.singletonMap(Header.EVENT_TYPE.getValue(), EventType.UNSUBSCRIBE_EVENT.getValue()));
        log.debug("User {} unsubscribed from destination: {}", user.getUsername(), destination);
    }
}
