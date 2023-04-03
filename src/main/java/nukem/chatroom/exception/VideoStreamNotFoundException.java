package nukem.chatroom.exception;

public class VideoStreamNotFoundException extends RuntimeException {
    public VideoStreamNotFoundException(String streamerUsername) {
        super(String.format("Video stream not found for streamer with username %s",streamerUsername));
    }
}
