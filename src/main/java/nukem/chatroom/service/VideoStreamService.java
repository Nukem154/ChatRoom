package nukem.chatroom.service;

import nukem.chatroom.dto.VideoStreamDto;
import nukem.chatroom.model.VideoStream;

public interface VideoStreamService {

    VideoStream getStreamById(Long id);

    VideoStreamDto startStream(Long chatRoomId);

    void endStreamByStreamerUsername(String username);

    VideoStreamDto watchStream(Long id);

    void stopWatchingStream(Long id);
}
