package nukem.chatroom.service.impl;

import nukem.chatroom.dto.chatroom.ChatRoomDetailedDto;
import nukem.chatroom.model.ChatRoom;
import nukem.chatroom.model.user.User;
import nukem.chatroom.repository.ChatRoomRepository;
import nukem.chatroom.repository.UserRepository;
import nukem.chatroom.service.ChatRoomService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class ChatRoomServiceTest {

    @Autowired
    private ChatRoomService chatRoomService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ChatRoomRepository chatRoomRepository;

    private ChatRoom chatRoom;

    @Test
    @Transactional
    public void testCreateChatRoom() {
        setupAuthentication();

        String roomName = "Test Room";
        ChatRoom chatRoom = new ChatRoom(roomName);
        ChatRoom savedChatRoom = chatRoomService.createChatRoom(roomName);

        assertNotNull(savedChatRoom.getId());
        assertEquals(chatRoom.getName(), savedChatRoom.getName());

        ChatRoomDetailedDto retrievedChatRoom = chatRoomService.getChatRoomInfo(savedChatRoom.getId());
        assertNotNull(retrievedChatRoom);
        assertEquals(savedChatRoom.getId(), retrievedChatRoom.getId());
        assertEquals(savedChatRoom.getName(), retrievedChatRoom.getName());
        assertEquals(1, retrievedChatRoom.getUsers().size());
    }

    @Test
    @Transactional
    public void testJoinChatRoom() {
        setupAuthentication();
        setupChatroom();

        // join the user to the chat room
        chatRoomService.joinChatRoom(chatRoom.getId());

        // retrieve the updated chat room from the database
        ChatRoom retrievedChatRoom = chatRoomRepository.findById(chatRoom.getId()).orElse(null);

        // verify that the user has been added to the chat room
        assertNotNull(retrievedChatRoom);
        assertEquals(1, retrievedChatRoom.getUsers().size());
    }

    @Test
    @Transactional
    public void testLeaveChatRoom() {
        setupAuthentication();
        setupChatroom();

        // retrieve the chat room from the database
        ChatRoom retrievedChatRoom = chatRoomRepository.findById(chatRoom.getId()).orElse(null);

        // verify that there is no users in the chat room
        assertNotNull(retrievedChatRoom);
        assertEquals(0, retrievedChatRoom.getUsers().size());

        // join the user to the chat room
        chatRoomService.joinChatRoom(chatRoom.getId());

        // verify that the user has been added to the chat room
        assertEquals(1, retrievedChatRoom.getUsers().size());

        // leave the chat room
        chatRoomService.leaveChatRoom(chatRoom.getId());

        // verify that the user has been removed from the chat room
        assertEquals(0, retrievedChatRoom.getUsers().size());
    }

    private void setupAuthentication() {
        // create a user
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("testpassword");
        userRepository.save(user);

        // create an authentication token for the user
        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void setupChatroom() {
        // create a chat room and join the user to it
        chatRoom = chatRoomRepository.save(new ChatRoom("Test Room"));
    }

}
