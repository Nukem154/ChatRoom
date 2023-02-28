package nukem.chatroom.exception;

public class UserAlreadyInRoomException extends RuntimeException {
    public UserAlreadyInRoomException(String username) {
        super(String.format("User '%s' is already in the chat room", username));
    }
}
