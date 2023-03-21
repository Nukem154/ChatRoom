package nukem.chatroom.enums.headers;

public enum EventType {
    CHAT_MESSAGE("chat-message"),
    CHAT_MESSAGE_EDIT("chat-message-edit"),
    CHAT_MESSAGE_DELETE("chat-message-delete"),
    SUBSCRIBE_EVENT("subscribe-event"),
    UNSUBSCRIBE_EVENT("unsubscribe-event"),
    STREAM_STARTED_EVENT("stream-started-event"),
    STREAM_ENDED_EVENT("stream-ended-event");

    private final String value;

    EventType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

