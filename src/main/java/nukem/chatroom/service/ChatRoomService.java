package nukem.chatroom.service;

import nukem.chatroom.model.ChatRoom;

import java.util.List;

public interface ChatRoomService {
    ChatRoom createChatRoom(String name);

    ChatRoom getChatRoom(Long id);

    List<ChatRoom> getChatRooms();

    void joinChatRoom(Long chatRoomId);

    void leaveChatRoom(Long chatRoomId);

}
