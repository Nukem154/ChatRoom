package nukem.chatroom.exception;

public class UserAlreadyInRoomException extends RuntimeException {
    public UserAlreadyInRoomException() {
        super("User is already in the chat room");
    }
}
