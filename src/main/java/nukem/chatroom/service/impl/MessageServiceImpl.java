package nukem.chatroom.service.impl;

import lombok.RequiredArgsConstructor;
import nukem.chatroom.dto.MessageDto;
import nukem.chatroom.dto.request.SendMessageRequest;
import nukem.chatroom.model.ChatRoom;
import nukem.chatroom.model.Message;
import nukem.chatroom.repository.ChatRoomRepository;
import nukem.chatroom.repository.MessageRepository;
import nukem.chatroom.service.AuthService;
import nukem.chatroom.service.MessageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static nukem.chatroom.constants.Constants.SLASH;
import static nukem.chatroom.constants.WebSocketURL.CHATROOMS;
import static nukem.chatroom.constants.WebSocketURL.MESSAGES;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    public static final String YOU_CAN_EDIT_DELETE_ONLY_YOUR_MESSAGES = "You can edit/delete only your messages";

    private final AuthService authService;
    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional(readOnly = true)
    public Page<Message> getUserMessagesOrderByIdDesc(final PageRequest pageRequest) {
        return messageRepository.findAllByUserIdOrderByIdDesc(authService.getCurrentUser().getId(), pageRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MessageDto> getMessagesByChatRoomId(final Long chatRoomId, final PageRequest pageRequest) {
        return messageRepository.findAllByChatRoomIdOrderByIdDesc(chatRoomId, pageRequest).map(MessageDto::toDto);
    }

    @Override
    @Transactional
    public Message sendMessage(final Long chatRoomId, final SendMessageRequest sendMessageRequest) {
        final ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow();
        final Message message = Message.builder()
                .chatRoom(chatRoom)
                .content(sendMessageRequest.content())
                .user(authService.getCurrentUser())
                .date(LocalDateTime.now())
                .build();
        messagingTemplate.convertAndSend(CHATROOMS + SLASH + chatRoomId + MESSAGES, MessageDto.toDto(message));
        return messageRepository.save(message);
    }

    @Override
    @Transactional
    public void deleteMessage(final Long id) {
        final Message message = messageRepository.findById(id).orElseThrow();
        verifyMessageOwnership(message);
        messageRepository.delete(message);
    }

    @Override
    @Transactional
    public Message editMessage(final Long id, final SendMessageRequest sendMessageRequest) {
        final Message message = messageRepository.findById(id).orElseThrow();
        verifyMessageOwnership(message);
        message.setContent(sendMessageRequest.content());
        return messageRepository.save(message);
    }

    private void verifyMessageOwnership(final Message message) {
        if (authService.getCurrentUser() != message.getUser()) {
            throw new AccessDeniedException(YOU_CAN_EDIT_DELETE_ONLY_YOUR_MESSAGES);
        }
    }
}
