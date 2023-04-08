package nukem.chatroom.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nukem.chatroom.dto.chatroom.ChatRoomDetailedDto;
import nukem.chatroom.dto.chatroom.ChatRoomMemberDto;
import nukem.chatroom.dto.chatroom.ChatRoomShortDto;
import nukem.chatroom.dto.request.CreateRoomRequest;
import nukem.chatroom.dto.videostream.VideoStreamDto;
import nukem.chatroom.model.ChatRoom;
import nukem.chatroom.repository.ChatRoomRepository;
import nukem.chatroom.service.AuthService;
import nukem.chatroom.service.ChatRoomService;
import nukem.chatroom.service.UserService;
import nukem.chatroom.utils.ErrorMessageUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static nukem.chatroom.constants.Constants.CHATROOM;
import static nukem.chatroom.constants.Constants.SLASH;
import static nukem.chatroom.constants.WebSocketURL.CHATROOMS;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final AuthService authService;
    private final SimpUserRegistry userRegistry;
    private final UserService userService;

    @Override
    @Transactional(readOnly = true)
    public ChatRoom getChatRoomById(final Long chatRoomId) {
        return chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessageUtils.entityNotFound(CHATROOM, chatRoomId)));
    }

    @Override
    @Transactional
    public ChatRoomDetailedDto createChatRoom(final CreateRoomRequest request) {
        final ChatRoom chatRoom = ChatRoom.builder()
                .name(request.name())
                .description(request.description())
                .owner(authService.getCurrentUser())
                .build();
        return ChatRoomDetailedDto.toDto(chatRoomRepository.save(chatRoom));
    }

    @Override
    @Transactional(readOnly = true)
    public ChatRoomDetailedDto getChatRoomInfo(final Long id) {
        final ChatRoom chatRoom = getChatRoomById(id);
        final ChatRoomDetailedDto chatRoomDetailedDto = ChatRoomDetailedDto.toDto(chatRoom);
        chatRoomDetailedDto.setActiveUsers(getActiveUsersDto(chatRoom));
        return chatRoomDetailedDto;
    }

    private List<ChatRoomMemberDto> getActiveUsersDto(final ChatRoom chatRoom) {
        final List<ChatRoomMemberDto> activeUsers = getActiveUsersInRoom(chatRoom.getId()).stream()
                .map(username -> ChatRoomMemberDto.toDto(userService.getUserByUsername(username)))
                .collect(Collectors.toList());

        final Map<String, ChatRoomMemberDto> userDtoMap = activeUsers.stream()
                .collect(Collectors.toMap(ChatRoomMemberDto::getUsername, Function.identity()));

        chatRoom.getVideoStreams().forEach(stream -> {
            ChatRoomMemberDto userDto = userDtoMap.get(stream.getUser().getUsername());
            if (userDto != null) {
                userDto.setStream(VideoStreamDto.toDto(stream));
            }
        });
        return activeUsers;
    }

    protected Set<String> getActiveUsersInRoom(final Long roomId) {
        return userRegistry.findSubscriptions(subscription -> subscription.getDestination().equals(getChatRoomTopic(roomId)))
                .stream()
                .map(subscription -> subscription.getSession().getUser().getName())
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ChatRoomShortDto> findAllChatRoomsByFilterParams(final String name, final Pageable pageable) {
        if (name != null) {
            return chatRoomRepository.findAllByNameContaining(name, pageable).map(ChatRoomShortDto::toDto);
        }
        return chatRoomRepository.findAll(pageable).map(ChatRoomShortDto::toDto);
    }

    public static String getChatRoomTopic(final Long chatRoomId) {
        return CHATROOMS + SLASH + chatRoomId;
    }
}
