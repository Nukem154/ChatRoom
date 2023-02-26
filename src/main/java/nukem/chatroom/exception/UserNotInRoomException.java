package nukem.chatroom.exception;

public class UserNotInRoomException extends RuntimeException {
    public UserNotInRoomException(String username) {
        super(String.format("User '%s' is not in the chat room", username));
    }
}
