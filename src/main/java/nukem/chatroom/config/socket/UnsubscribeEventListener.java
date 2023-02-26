package nukem.chatroom.config.socket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import static nukem.chatroom.constants.Constants.SLASH;
import static nukem.chatroom.constants.WebSocketURL.CHATROOMS;
import static nukem.chatroom.constants.WebSocketURL.USERS;

@Component
@RequiredArgsConstructor
@Slf4j
public class UnsubscribeEventListener implements ApplicationListener<SessionUnsubscribeEvent> {

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void onApplicationEvent(SessionUnsubscribeEvent event) {
        final String destination = CHATROOMS + SLASH + event.getMessage().getHeaders().get("simpSubscriptionId").toString();
        final String username = event.getUser().getName();
        messagingTemplate.convertAndSend(destination + USERS, "USER UNSUBSCRIBED: " + username);
        log.info("User {} unsubscribed from destination: {}", username, destination);
    }
}
