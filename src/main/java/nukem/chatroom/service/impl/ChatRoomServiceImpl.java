package nukem.chatroom.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nukem.chatroom.dto.UserDto;
import nukem.chatroom.dto.chatroom.ChatRoomDetailedDto;
import nukem.chatroom.dto.chatroom.ChatRoomShortDto;
import nukem.chatroom.dto.request.CreateRoomRequest;
import nukem.chatroom.model.ChatRoom;
import nukem.chatroom.repository.ChatRoomRepository;
import nukem.chatroom.service.AuthService;
import nukem.chatroom.service.ChatRoomService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static nukem.chatroom.constants.Constants.SLASH;
import static nukem.chatroom.constants.WebSocketURL.CHATROOMS;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final AuthService authService;
    private final SimpUserRegistry userRegistry;


    @Override
    @Transactional
    public ChatRoom createChatRoom(final CreateRoomRequest request) {
        final ChatRoom chatRoom = ChatRoom.builder()
                .name(request.name())
                .description(request.description())
                .owner(authService.getCurrentUser())
                .build();
        return chatRoomRepository.save(chatRoom);
    }

    @Override
    @Transactional(readOnly = true)
    public ChatRoomDetailedDto getChatRoomInfo(final Long id) {
        return chatRoomRepository.findById(id).map(chatRoom -> {
            var chatRoomDetailedDto = ChatRoomDetailedDto.toDto(chatRoom);
            chatRoomDetailedDto.setUsers(getActiveUsersDto(id, chatRoom));
            return chatRoomDetailedDto;
        }).orElseThrow(EntityNotFoundException::new);
    }

    private Set<UserDto> getActiveUsersDto(final Long id, final ChatRoom chatRoom) {
        final Set<UserDto> usersDto = getActiveUsersInRoom(id).stream()
                .map(username -> UserDto.builder().username(username).build())
                .collect(Collectors.toSet());

        final Map<String, UserDto> userDtoMap = usersDto.stream()
                .collect(Collectors.toMap(UserDto::getUsername, Function.identity()));

        chatRoom.getStreams().forEach(stream -> {
            UserDto userDto = userDtoMap.get(stream.getUser().getUsername());
            if (userDto != null) {
                userDto.setUserStreaming(true);
            }
        });
        return usersDto;
    }

    private Set<String> getActiveUsersInRoom(final Long roomId) {
        return userRegistry.findSubscriptions(subscription -> subscription.getDestination().equals(CHATROOMS + SLASH + roomId))
                .stream()
                .map(subscription -> subscription.getSession().getUser().getName())
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ChatRoomShortDto> findAllChatRoomsByFilterParams(final String name, final Pageable pageRequest) {
        if (name != null) {
            return chatRoomRepository.findAllByNameContainingOrderByIdDesc(name, pageRequest).map(ChatRoomShortDto::toDto);
        }
        return chatRoomRepository.findAllByOrderByIdDesc(pageRequest).map(ChatRoomShortDto::toDto);
    }

    public static String getChatRoomTopic(final Long chatRoomId) {
        return CHATROOMS + SLASH + chatRoomId;
    }
}
