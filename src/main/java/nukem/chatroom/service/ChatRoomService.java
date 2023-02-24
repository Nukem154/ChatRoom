package nukem.chatroom.service;

import nukem.chatroom.dto.chatroom.ChatRoomDetailedDto;
import nukem.chatroom.dto.chatroom.ChatRoomShortDto;
import nukem.chatroom.dto.request.CreateRoomRequest;
import nukem.chatroom.model.ChatRoom;

import java.util.List;

public interface ChatRoomService {
    ChatRoom createChatRoom(CreateRoomRequest request);

    ChatRoomDetailedDto getChatRoomInfo(Long id);

    List<ChatRoomShortDto> getChatRooms();

    void joinChatRoom(Long chatRoomId);

    void leaveChatRoom(Long chatRoomId);

}
