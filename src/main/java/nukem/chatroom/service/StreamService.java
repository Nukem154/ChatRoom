package nukem.chatroom.service;

public interface StreamService {
    void startStream(Long chatRoomId);

    void endStream(String username);
}
