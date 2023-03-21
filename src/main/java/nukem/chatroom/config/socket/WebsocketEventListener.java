package nukem.chatroom.config.socket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nukem.chatroom.enums.headers.EventType;
import nukem.chatroom.service.StreamService;
import nukem.chatroom.service.WebsocketService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static nukem.chatroom.constants.Constants.SLASH;
import static nukem.chatroom.constants.WebSocketURL.CHATROOMS;
import static nukem.chatroom.constants.WebSocketURL.STREAMER;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebsocketEventListener {
    private final ConcurrentHashMap<String, Set<String>> subscriptions = new ConcurrentHashMap<>();

    private final StreamService streamService;
    private final WebsocketService websocketService;

    @EventListener
    public void handleSessionSubscribeEvent(SessionSubscribeEvent event) {
        final String destination = event.getMessage().getHeaders().get("simpDestination").toString();
        final String username = event.getUser().getName();

        if (isChatroomDestination(destination)) {
            websocketService.notifyWebsocketSubscribers(destination, username, EventType.SUBSCRIBE_EVENT);
            log.debug("User {} subscribed to destination: {}", username, destination);
        }

        if (isStreamerDestination(destination)) {
            subscriptions.computeIfAbsent(STREAMER, k -> ConcurrentHashMap.newKeySet()).add(username);
        }
    }

    @EventListener
    public void handleSessionUnsubscribeEvent(SessionUnsubscribeEvent event) {
        final String destination = CHATROOMS + SLASH + event.getMessage().getHeaders().get("simpSubscriptionId").toString();
        final String username = event.getUser().getName();

        if (isChatroomDestination(destination)) {
            websocketService.notifyWebsocketSubscribers(destination, username, EventType.UNSUBSCRIBE_EVENT);
            log.debug("User {} unsubscribed from destination: {}", username, destination);
        }

        if (isStreamerDestination(destination) && subscriptions.containsKey(STREAMER)) {
            subscriptions.get(STREAMER).remove(username);
        }
    }

    @EventListener
    public void handleSessionDisconnectEvent(SessionDisconnectEvent event) {
        if (subscriptions.get(STREAMER) != null) {
            if (subscriptions.get(STREAMER).contains(event.getUser().getName())) {
                streamService.endStream(event.getUser().getName());
            }
        }
    }

    private boolean isChatroomDestination(final String destination) {
        final String[] segments = destination.split("/");
        return segments.length == 3 && segments[1].equals("chatrooms");
    }

    private boolean isStreamerDestination(final String destination) {
        return destination.contains(STREAMER);
    }
}
