package nukem.chatroom.config.socket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import static nukem.chatroom.constants.WebSocketURL.USERS;

@Component
@RequiredArgsConstructor
@Slf4j
public class SubscribeEventListener implements ApplicationListener<SessionSubscribeEvent> {

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void onApplicationEvent(SessionSubscribeEvent event) {
        final String destination = event.getMessage().getHeaders().get("simpDestination").toString();
        final String username = event.getUser().getName();
        messagingTemplate.convertAndSend(destination + USERS, "USER SUBSCRIBED: " + username);
        log.info("User {} subscribed to destination: {}", username, destination);
    }
}
