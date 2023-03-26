package nukem.chatroom.config.socket;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class SubscriptionTracker {
    private final Map<String, Set<String>> subscriptions = new ConcurrentHashMap<>();

    public void addSubscriber(final String destination, final String subscriber) {
        subscriptions.computeIfAbsent(destination, k -> ConcurrentHashMap.newKeySet()).add(subscriber);
    }

    public void removeSubscriber(final String destination, final String subscriber) {
        final Set<String> subscribers = subscriptions.get(destination);
        if (subscribers != null) {
            subscribers.remove(subscriber);
        }
    }

    public boolean isSubscriber(final String destination, final String subscriber) {
        final Set<String> subscribers = subscriptions.get(destination);
        return subscribers != null && subscribers.contains(subscriber);
    }
}
