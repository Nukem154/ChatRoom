package nukem.chatroom.service;

import nukem.chatroom.dto.chatroom.ChatRoomDetailedDto;
import nukem.chatroom.dto.chatroom.ChatRoomShortDto;
import nukem.chatroom.dto.request.CreateRoomRequest;
import nukem.chatroom.model.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChatRoomService {

    ChatRoom getChatRoomById(Long chatRoomId);

    ChatRoomDetailedDto createChatRoom(CreateRoomRequest request);

    ChatRoomDetailedDto getChatRoomInfo(Long id);

    Page<ChatRoomShortDto> findAllChatRoomsByFilterParams(String name, Pageable pageRequest);

}
