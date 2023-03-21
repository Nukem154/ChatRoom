package nukem.chatroom.service.impl;

import lombok.RequiredArgsConstructor;
import nukem.chatroom.enums.headers.EventType;
import nukem.chatroom.model.ChatRoom;
import nukem.chatroom.model.Stream;
import nukem.chatroom.model.user.User;
import nukem.chatroom.repository.ChatRoomRepository;
import nukem.chatroom.repository.StreamRepository;
import nukem.chatroom.service.AuthService;
import nukem.chatroom.service.StreamService;
import nukem.chatroom.service.WebsocketService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static nukem.chatroom.service.impl.ChatRoomServiceImpl.getChatRoomTopic;

@Service
@RequiredArgsConstructor
public class StreamServiceImpl implements StreamService {

    private final StreamRepository streamRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final AuthService authService;
    private final WebsocketService websocketService;

    @Override
    @Transactional
    public void startStream(final Long chatRoomId) {
        final ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow();
        final User user = authService.getCurrentUser();

        final Stream stream = new Stream();
        stream.setChatRoom(chatRoom);
        stream.setUser(user);

        streamRepository.save(stream);

        websocketService.notifyWebsocketSubscribers(getChatRoomTopic(stream.getChatRoom().getId()),
                user.getUsername(), EventType.STREAM_STARTED_EVENT);
    }

    @Override
    @Transactional
    public void endStream(final String username) {
        streamRepository.findByUserUsername(username).ifPresent(
                stream -> {
                    streamRepository.deleteByUserUsername(username);
                    websocketService.notifyWebsocketSubscribers(getChatRoomTopic(stream.getChatRoom().getId()),
                            username, EventType.STREAM_ENDED_EVENT);
                }
        );
    }
}
