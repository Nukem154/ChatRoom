package nukem.chatroom.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nukem.chatroom.dto.chatroom.ChatRoomDetailedDto;
import nukem.chatroom.dto.chatroom.ChatRoomShortDto;
import nukem.chatroom.dto.request.CreateRoomRequest;
import nukem.chatroom.exception.UserAlreadyInRoomException;
import nukem.chatroom.exception.UserNotInRoomException;
import nukem.chatroom.model.ChatRoom;
import nukem.chatroom.model.user.User;
import nukem.chatroom.repository.ChatRoomRepository;
import nukem.chatroom.service.AuthService;
import nukem.chatroom.service.ChatRoomService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static nukem.chatroom.constants.Constants.SLASH;
import static nukem.chatroom.constants.WebSocketURL.CHATROOMS;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final AuthService authService;
    private final SimpMessagingTemplate messagingTemplate;
    private final SimpUserRegistry userRegistry;


    @Override
    @Transactional
    public ChatRoom createChatRoom(final CreateRoomRequest request) {
        final ChatRoom chatRoom = new ChatRoom(request.name(), request.description());
        final ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
        joinChatRoom(savedChatRoom.getId());
        return savedChatRoom;
    }

    @Override
    @Transactional(readOnly = true)
    public ChatRoomDetailedDto getChatRoomInfo(final Long id) {
        return chatRoomRepository.findById(id).map(ChatRoomDetailedDto::toDto).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatRoomShortDto> getChatRooms() {
        return chatRoomRepository.findAll().stream().map(ChatRoomShortDto::toDto).collect(Collectors.toList());
    }

    @Override
    public Set<String> getActiveUsersInRoom(final Long roomId) {
        return userRegistry.findSubscriptions(subscription -> subscription.getDestination().equals(CHATROOMS + SLASH + roomId)).stream()
                .map(subscription -> subscription.getSession().getUser().getPrincipal().getName())
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public void joinChatRoom(final Long chatRoomId) {
        performChatRoomAction(chatRoomId, (chatRoom, user) -> {
            if (chatRoom.getUsers().contains(user)) {
                throw new UserAlreadyInRoomException(user.getUsername());
            }
            chatRoom.getUsers().add(user);
            messagingTemplate.convertAndSend(getChatRoomTopic(chatRoomId), getUserJoinMessage(user));
        });
    }

    @Override
    @Transactional
    public void leaveChatRoom(final Long chatRoomId) {
        performChatRoomAction(chatRoomId, (chatRoom, user) -> {
            if (!chatRoom.getUsers().contains(user)) {
                throw new UserNotInRoomException(user.getUsername());
            }
            chatRoom.getUsers().remove(user);
            messagingTemplate.convertAndSend(getChatRoomTopic(chatRoomId), getUserLeaveMessage(user));
        });
    }

    private void performChatRoomAction(final Long chatRoomId, final BiConsumer<ChatRoom, User> action) {
        final ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(EntityNotFoundException::new);
        final User user = authService.getCurrentUser();
        action.accept(chatRoom, user);
        chatRoomRepository.save(chatRoom);
    }

    private String getChatRoomTopic(final Long chatRoomId) {
        return CHATROOMS + SLASH + chatRoomId;
    }

    private String getUserJoinMessage(final User user) {
        return user.getUsername() + " joined the room";
    }

    private String getUserLeaveMessage(final User user) {
        return user.getUsername() + " left the room";
    }
}
