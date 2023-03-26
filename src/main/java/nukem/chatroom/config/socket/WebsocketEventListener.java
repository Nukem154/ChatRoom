package nukem.chatroom.config.socket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nukem.chatroom.enums.headers.EventType;
import nukem.chatroom.service.StreamService;
import nukem.chatroom.service.WebsocketService;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import static nukem.chatroom.constants.WebSocketURL.STREAMER;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebsocketEventListener {

    private final SubscriptionTracker subscriptionTracker;
    private final StreamService streamService;
    private final WebsocketService websocketService;

    @EventListener
    public void handleSessionSubscribeEvent(SessionSubscribeEvent event) {
        final String destination = SimpMessageHeaderAccessor.wrap(event.getMessage()).getDestination();

        if (destination != null) {
            final String username = event.getUser().getName();

            if (isChatroomDestination(destination)) {
                websocketService.notifyWebsocketSubscribers(destination, username, EventType.SUBSCRIBE_EVENT);
                log.debug("User {} subscribed to destination: {}", username, destination);
            } else if (isStreamerDestination(destination)) {
                subscriptionTracker.addSubscriber(STREAMER, username);
            }
        }
    }

    @EventListener
    public void handleSessionUnsubscribeEvent(final SessionUnsubscribeEvent event) {
        final String destination = SimpMessageHeaderAccessor.wrap(event.getMessage()).getSubscriptionId();

        if (destination != null) {
            final String username = event.getUser().getName();

            if (isChatroomDestination(destination)) {
                websocketService.notifyWebsocketSubscribers(destination, username, EventType.UNSUBSCRIBE_EVENT);
                log.debug("User {} unsubscribed from destination: {}", username, destination);
            } else if (isStreamerDestination(destination)) {
                subscriptionTracker.removeSubscriber(STREAMER, username);
            }
        }
    }

    @EventListener
    public void handleSessionDisconnectEvent(final SessionDisconnectEvent event) {
        endStreamIfNeeded(event.getUser().getName());
    }

    private void endStreamIfNeeded(String username) {
        if (subscriptionTracker.isSubscriber(STREAMER, username)) {
            streamService.endStream(username);
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
