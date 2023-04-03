package nukem.chatroom.service.impl;

import lombok.RequiredArgsConstructor;
import nukem.chatroom.dto.videostream.StreamViewershipDto;
import nukem.chatroom.dto.videostream.VideoStreamDto;
import nukem.chatroom.enums.headers.EventType;
import nukem.chatroom.exception.VideoStreamNotFoundException;
import nukem.chatroom.model.VideoStream;
import nukem.chatroom.model.user.User;
import nukem.chatroom.repository.StreamRepository;
import nukem.chatroom.service.AuthService;
import nukem.chatroom.service.ChatRoomService;
import nukem.chatroom.service.VideoStreamService;
import nukem.chatroom.service.WebsocketService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static nukem.chatroom.service.impl.ChatRoomServiceImpl.getChatRoomTopic;

@Service
@RequiredArgsConstructor
public class VideoStreamServiceImpl implements VideoStreamService {

    private final StreamRepository streamRepository;
    private final ChatRoomService chatRoomService;
    private final AuthService authService;
    private final WebsocketService websocketService;

    @Override
    @Transactional(readOnly = true)
    public VideoStream getStreamByStreamerUsername(final String username) {
        return streamRepository.findByUserUsername(username)
                .orElseThrow(() -> new VideoStreamNotFoundException(username));
    }

    @Override
    @Transactional
    public VideoStreamDto startStream(final Long chatRoomId) {
        final VideoStream videoStream = VideoStream.builder()
                .chatRoom(chatRoomService.getChatRoomById(chatRoomId))
                .user(authService.getCurrentUser())
                .build();

        websocketService.notifyWebsocketSubscribers(getChatRoomTopic(videoStream.getChatRoom().getId()),
                videoStream.getUser().getUsername(), EventType.STREAM_STARTED_EVENT);

        return VideoStreamDto.toDto(streamRepository.save(videoStream));
    }

    @Override
    @Transactional
    public void endStreamByStreamerUsername(final String username) {
        streamRepository.findByUserUsername(username).ifPresent(
                stream -> {
                    streamRepository.deleteByUserUsername(username);
                    websocketService.notifyWebsocketSubscribers(getChatRoomTopic(stream.getChatRoom().getId()),
                            username, EventType.STREAM_ENDED_EVENT);
                }
        );
    }

    @Override
    @Transactional
    public VideoStreamDto watchStream(final String streamerUsername) {
        final VideoStream videoStream = getStreamByStreamerUsername(streamerUsername);
        final User user = authService.getCurrentUser();

        videoStream.getViewers().add(user);

        websocketService.notifyWebsocketSubscribers(getChatRoomTopic(videoStream.getChatRoom().getId()),
                new StreamViewershipDto(user.getUsername(), videoStream.getUser().getUsername()), EventType.STREAM_VIEWER_JOINED);

        return VideoStreamDto.toDto(streamRepository.save(videoStream));
    }

    @Override
    @Transactional
    public void stopWatchingStream(final String streamerUsername) {
        final VideoStream videoStream = getStreamByStreamerUsername(streamerUsername);
        final User user = authService.getCurrentUser();

        videoStream.getViewers().remove(user);

        websocketService.notifyWebsocketSubscribers(getChatRoomTopic(videoStream.getChatRoom().getId()),
                new StreamViewershipDto(user.getUsername(), videoStream.getUser().getUsername()), EventType.STREAM_VIEWER_LEFT);

        streamRepository.save(videoStream);
    }
}
