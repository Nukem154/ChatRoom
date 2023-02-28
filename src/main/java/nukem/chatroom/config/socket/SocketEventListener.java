package nukem.chatroom.config.socket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nukem.chatroom.enums.headers.EventType;
import nukem.chatroom.enums.headers.Header;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.util.Collections;

import static nukem.chatroom.constants.Constants.SLASH;
import static nukem.chatroom.constants.WebSocketURL.CHATROOMS;
import static nukem.chatroom.constants.WebSocketURL.USERS;

@Component
@RequiredArgsConstructor
@Slf4j
public class SocketEventListener {

    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void handleSessionSubscribeEvent(SessionSubscribeEvent event) {
        final String destination = event.getMessage().getHeaders().get("simpDestination").toString();
        final String username = event.getUser().getName();
        messagingTemplate.convertAndSend(destination + USERS, username,
                Collections.singletonMap(Header.EVENT_TYPE.getValue(), EventType.SUBSCRIBE_EVENT.getValue()));
        log.info("User {} subscribed to destination: {}", username, destination);
    }

    @EventListener
    public void handleSessionUnsubscribeEvent(SessionUnsubscribeEvent event) {
        final String destination = CHATROOMS + SLASH + event.getMessage().getHeaders().get("simpSubscriptionId").toString();
        final String username = event.getUser().getName();
        messagingTemplate.convertAndSend(destination + USERS, username,
                Collections.singletonMap(Header.EVENT_TYPE.getValue(), EventType.UNSUBSCRIBE_EVENT.getValue()));
        log.info("User {} unsubscribed from destination: {}", username, destination);
    }
}
