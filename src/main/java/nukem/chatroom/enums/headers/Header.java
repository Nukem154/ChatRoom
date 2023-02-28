package nukem.chatroom.enums.headers;

public enum Header {
    EVENT_TYPE("event-type");
    private final String value;

    Header(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
