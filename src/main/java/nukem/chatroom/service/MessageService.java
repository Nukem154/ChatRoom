package nukem.chatroom.service;

import nukem.chatroom.dto.MessageDto;
import nukem.chatroom.dto.request.SendMessageRequest;
import nukem.chatroom.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MessageService {

    Message getMessageById(Long id);

    Page<Message> getUserMessagesOrderByIdDesc(Pageable pageRequest);

    Message sendMessage(Long chatRoomId, SendMessageRequest message);

    void deleteMessage(Long id);

    Message editMessage(Long id, SendMessageRequest sendMessageRequest);

    Page<MessageDto> getMessagesByChatRoomId(Long chatRoomId, Pageable pageRequest);
}
