package nukem.chatroom.service;

import nukem.chatroom.enums.headers.EventType;

public interface WebsocketService {

    /**
     * Notifies all WebSocket subscribers to the specified destination about the event performed.
     *
     * @param destination the destination to which the message was sent
     * @param payload     the payload object for the event
     * @param event       the type of event that was performed
     */
    void notifyWebsocketSubscribers(String destination, Object payload, EventType event);
}
