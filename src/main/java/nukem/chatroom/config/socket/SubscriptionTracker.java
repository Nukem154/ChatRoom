package nukem.chatroom.config.socket;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A class that tracks websocket subscriptions for each destination.
 */
@Component
@RequiredArgsConstructor
public class SubscriptionTracker {
    /**
     * A map that stores the set of subscribers for each destination.
     * The map is thread-safe, using a ConcurrentHashMap to avoid concurrency issues.
     */
    private final Map<String, Set<String>> subscriptions = new ConcurrentHashMap<>();

    /**
     * Adds a subscriber to the set of subscribers for the given destination.
     * If the destination does not exist in the map, a new set is created for it.
     *
     * @param destination the destination to add the subscriber to
     * @param subscriber  the name of the subscriber to add
     */
    public void addSubscriber(final String destination, final String subscriber) {
        subscriptions.computeIfAbsent(destination, k -> ConcurrentHashMap.newKeySet()).add(subscriber);
    }

    /**
     * Removes a subscriber from the set of subscribers for the given destination.
     * If the destination does not exist in the map, no action is taken.
     *
     * @param destination the destination to remove the subscriber from
     * @param subscriber  the name of the subscriber to remove
     */
    public void removeSubscriber(final String destination, final String subscriber) {
        final Set<String> subscribers = subscriptions.get(destination);
        if (subscribers != null) {
            subscribers.remove(subscriber);
        }
    }

    /**
     * Returns true if the given subscriber is subscribed to the given destination,
     * and false otherwise. If the destination does not exist in the map, returns false.
     *
     * @param destination the destination to check for subscription
     * @param subscriber  the name of the subscriber to check for
     * @return true if the subscriber is subscribed to the destination, false otherwise
     */
    public boolean isSubscriber(final String destination, final String subscriber) {
        final Set<String> subscribers = subscriptions.get(destination);
        return subscribers != null && subscribers.contains(subscriber);
    }
}
