package nukem.chatroom.exception;

public class VideoStreamAlreadyInLiveStateException extends RuntimeException {
    public VideoStreamAlreadyInLiveStateException() {
        super("Cannot start the stream as it is already in a live state. " +
                "Please stop the ongoing stream before starting a new one.");
    }
}

