package nukem.chatroom.service;

import nukem.chatroom.dto.MessageDto;
import nukem.chatroom.dto.request.MessageRequest;
import nukem.chatroom.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface MessageService {
    Page<Message> getUserMessages(PageRequest pageRequest);

    Message sendMessage(Long chatRoomId, MessageRequest message);

    void deleteMessage(Long id);

    Message editMessage(Long id, MessageRequest messageRequest);

    Page<MessageDto> getMessagesByChatRoomId(Long chatRoomId, PageRequest pageRequest);
}
