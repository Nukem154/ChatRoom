package nukem.chatroom.service.impl;

import nukem.chatroom.dto.request.SendMessageRequest;
import nukem.chatroom.enums.headers.EventType;
import nukem.chatroom.model.ChatRoom;
import nukem.chatroom.model.Message;
import nukem.chatroom.model.user.User;
import nukem.chatroom.repository.ChatRoomRepository;
import nukem.chatroom.repository.MessageRepository;
import nukem.chatroom.service.AuthService;
import nukem.chatroom.service.ChatRoomService;
import nukem.chatroom.service.WebsocketService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.Optional;

import static nukem.chatroom.service.impl.ChatRoomServiceImpl.getChatRoomTopic;
import static nukem.chatroom.service.impl.MessageServiceImpl.YOU_CAN_EDIT_DELETE_ONLY_YOUR_MESSAGES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MessageServiceImplTest {

    @Mock
    private AuthService authService;
    @Mock
    private MessageRepository messageRepository;
    @Mock
    private ChatRoomService chatRoomService;
    @Mock
    private WebsocketService websocketService;
    @InjectMocks
    private MessageServiceImpl messageService;

    private final Long ownerId = 156L;
    private final Long currentUserId = 12L;
    private final Long chatRoomId = 10L;
    private final Long messageId = 1L;

    @Test
    void sendMessageTest_shouldSaveMessageAndNotifySubscribers() {
        final SendMessageRequest sendMessageRequest = new SendMessageRequest("test message");
        final User user = User.builder()
                .id(1L)
                .build();
        final ChatRoom chatRoom = ChatRoom.builder()
                .id(chatRoomId)
                .build();
        final Message savedMessage = Message.builder()
                .id(1L)
                .chatRoom(chatRoom)
                .content(sendMessageRequest.content())
                .user(user)
                .date(LocalDateTime.now())
                .build();

        when(chatRoomService.getChatRoomById(chatRoomId)).thenReturn(chatRoom);
        when(authService.getCurrentUser()).thenReturn(user);
        when(messageRepository.save(any())).thenReturn(savedMessage);

        final Message result = messageService.sendMessage(chatRoomId, sendMessageRequest);

        verify(chatRoomService).getChatRoomById(chatRoomId);
        verify(authService).getCurrentUser();
        verify(messageRepository).save(any());
        verify(websocketService).notifyWebsocketSubscribers(eq(getChatRoomTopic(chatRoomId)), any(), eq(EventType.CHAT_MESSAGE));
        assertEquals(savedMessage, result);
    }

    @Test
    void deleteMessage_shouldDeleteMessageAndNotifySubscribers() {
        final User user = User.builder()
                .id(ownerId)
                .build();
        final Message message = Message.builder()
                .id(messageId)
                .chatRoom(ChatRoom.builder().id(chatRoomId).build())
                .user(user)
                .build();

        when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));
        when(authService.getCurrentUser()).thenReturn(user);

        messageService.deleteMessage(messageId);

        verify(messageRepository).delete(message);
        verify(websocketService).notifyWebsocketSubscribers(eq(getChatRoomTopic(chatRoomId)), any(), eq(EventType.CHAT_MESSAGE_DELETE));
    }

    @Test
    void deleteMessage_whenCurrentUserIsNotMessageOwner_thenThrowException() {
        final Message message = Message.builder()
                .id(messageId)
                .chatRoom(ChatRoom.builder().id(chatRoomId).build())
                .user(User.builder().id(ownerId).build())
                .build();

        when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));
        when(authService.getCurrentUser()).thenReturn(User.builder().id(currentUserId).build());

        final Exception exception = assertThrows(AccessDeniedException.class, () -> messageService.deleteMessage(messageId));

        assertEquals(YOU_CAN_EDIT_DELETE_ONLY_YOUR_MESSAGES, exception.getMessage());
        verify(messageRepository, never()).delete(any());
    }

    @Test
    void editMessage_shouldUpdateMessageAndNotifySubscribers() {
        final SendMessageRequest sendMessageRequest = new SendMessageRequest("updated message content");
        final User user = User.builder().id(ownerId).build();
        final ChatRoom chatRoom = ChatRoom.builder().id(chatRoomId).build();
        final Message message = Message.builder()
                .id(messageId)
                .chatRoom(chatRoom)
                .user(user)
                .date(LocalDateTime.now())
                .build();

        when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));
        when(authService.getCurrentUser()).thenReturn(user);

        messageService.editMessage(messageId, sendMessageRequest);

        verify(messageRepository).findById(messageId);
        verify(websocketService).notifyWebsocketSubscribers(eq(getChatRoomTopic(chatRoomId)), any(), eq(EventType.CHAT_MESSAGE_EDIT));
        verify(messageRepository).save(any());
    }

    @Test
    void editMessageTest_whenCurrentUserIsNotMessageOwner_thenThrowException() {
        final User messageOwner = User.builder()
                .id(ownerId)
                .build();
        final User currentUser = User.builder()
                .id(currentUserId)
                .build();
        final Message message = Message.builder()
                .id(messageId)
                .chatRoom(ChatRoom.builder().id(chatRoomId).build())
                .user(messageOwner)
                .content("old content")
                .build();
        final SendMessageRequest editMessageRequest = new SendMessageRequest("new content");

        when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));
        when(authService.getCurrentUser()).thenReturn(currentUser);

        final Exception exception = assertThrows(AccessDeniedException.class, () -> messageService.editMessage(messageId, editMessageRequest));
        assertEquals(YOU_CAN_EDIT_DELETE_ONLY_YOUR_MESSAGES, exception.getMessage());
        verify(messageRepository).findById(messageId);
        verify(authService).getCurrentUser();
        verifyNoMoreInteractions(websocketService);
    }
}