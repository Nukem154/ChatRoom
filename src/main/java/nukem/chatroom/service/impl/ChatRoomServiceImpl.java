package nukem.chatroom.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nukem.chatroom.dto.chatroom.ChatRoomDetailedDto;
import nukem.chatroom.dto.chatroom.ChatRoomShortDto;
import nukem.chatroom.exception.UserAlreadyInRoomException;
import nukem.chatroom.model.ChatRoom;
import nukem.chatroom.model.user.User;
import nukem.chatroom.repository.ChatRoomRepository;
import nukem.chatroom.service.AuthService;
import nukem.chatroom.service.ChatRoomService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final AuthService authService;
    private final SimpMessagingTemplate messagingTemplate;


    @Override
    @Transactional
    public ChatRoom createChatRoom(final String name) {
        final ChatRoom chatRoom = new ChatRoom(name);
        final ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
        joinChatRoom(savedChatRoom.getId());
        return savedChatRoom;
    }

    @Override
    @Transactional(readOnly = true)
    public ChatRoomDetailedDto getChatRoomInfo(final Long id) {
        return chatRoomRepository.findById(id).map(ChatRoomDetailedDto::toDto).orElseThrow();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatRoomShortDto> getChatRooms() {
        return chatRoomRepository.findAll().stream().map(ChatRoomShortDto::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void joinChatRoom(final Long chatRoomId) {
        final ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow();
        final User user = authService.getCurrentUser();
        if (chatRoom.getUsers().contains(user)) {
            throw new UserAlreadyInRoomException();
        }
        chatRoom.getUsers().add(user);
        chatRoomRepository.save(chatRoom);
        messagingTemplate.convertAndSend("/chatroom" + chatRoomId, user.getUsername() + "has joined");
        log.info("{} has joined the room {}", user.getUsername(), chatRoom.getName());
    }

    @Override
    @Transactional
    public void leaveChatRoom(final Long chatRoomId) {
        final ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow();
        final User user = authService.getCurrentUser();
        chatRoom.getUsers().remove(user);
        chatRoomRepository.save(chatRoom);
        messagingTemplate.convertAndSend("/chatroom" + chatRoomId, user.getUsername() + "has left");
        log.info("{} has left the room {}", user.getUsername(), chatRoom.getName());
    }
}
