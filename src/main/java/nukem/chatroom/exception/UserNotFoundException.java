package nukem.chatroom.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String username) {
        super(String.format("User not found: %s", username));
    }
}
