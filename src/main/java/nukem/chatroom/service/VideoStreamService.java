package nukem.chatroom.service;

import nukem.chatroom.dto.videostream.VideoStreamDto;
import nukem.chatroom.model.VideoStream;

public interface VideoStreamService {

    VideoStream getStreamByStreamerUsername(String username);

    VideoStreamDto startStream(Long chatRoomId);

    void endStreamByStreamerUsername(String username);

    VideoStreamDto watchStream(String streamerUsername);

    void stopWatchingStream(String streamerUsername);
}
