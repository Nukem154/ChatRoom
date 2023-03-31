package nukem.chatroom.service.impl;

import nukem.chatroom.dto.UserDto;
import nukem.chatroom.dto.chatroom.ChatRoomDetailedDto;
import nukem.chatroom.dto.chatroom.ChatRoomShortDto;
import nukem.chatroom.dto.request.CreateRoomRequest;
import nukem.chatroom.model.ChatRoom;
import nukem.chatroom.model.user.User;
import nukem.chatroom.repository.ChatRoomRepository;
import nukem.chatroom.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatRoomServiceImplTest {
    @Mock
    private ChatRoomRepository chatRoomRepository;
    @Mock
    private AuthService authService;
    @InjectMocks
    @Spy
    private ChatRoomServiceImpl chatRoomService;

    @Test
    void createChatRoomTest() {
        CreateRoomRequest request = new CreateRoomRequest("Test Room", "This is a test room");

        User currentUser = User.builder().username("testuser").build();

        when(authService.getCurrentUser()).thenReturn(currentUser);

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setName(request.name());
        chatRoom.setDescription(request.description());
        chatRoom.setOwner(currentUser);

        when(chatRoomRepository.save(any(ChatRoom.class))).thenReturn(chatRoom);

        ChatRoom result = chatRoomService.createChatRoom(request);

        assertEquals(request.name(), result.getName());
        assertEquals(request.description(), result.getDescription());
        assertEquals(currentUser, result.getOwner());
    }

    @Test
    void getChatRoomInfoTest() {
        Long roomId = 1L;
        ChatRoom chatRoom = ChatRoom.builder()
                .id(roomId)
                .name("Test Room")
                .description("This is a test room")
                .streams(new HashSet<>())
                .build();
        User owner = User.builder()
                .username("testuser")
                .build();

        chatRoom.setOwner(owner);

        when(chatRoomRepository.findById(roomId)).thenReturn(Optional.of(chatRoom));

        Set<String> activeUsers = Set.of("user1", "user2");
        doReturn(activeUsers).when(chatRoomService).getActiveUsersInRoom(roomId);

        ChatRoomDetailedDto result = chatRoomService.getChatRoomInfo(roomId);

        assertEquals(roomId, result.getId());
        assertEquals(chatRoom.getName(), result.getName());
        assertEquals(chatRoom.getDescription(), result.getDescription());
        assertEquals(owner.getUsername(), result.getOwner());
        assertEquals(activeUsers.size(), result.getActiveUsers().size());
        assertTrue(result.getActiveUsers().stream().map(UserDto::getUsername).allMatch(activeUsers::contains));
    }

    @Test
    void findAllChatRoomsByFilterParamsTest() {
        String name = "Test Room";
        Pageable pageRequest = PageRequest.of(0, 10);
        List<ChatRoom> chatRooms = List.of(
                ChatRoom.builder().id(1L).name("Test Room 1").description("This is test room 1").build(),
                ChatRoom.builder().id(2L).name("Test Room 2").description("This is test room 2").build()
        );

        when(chatRoomRepository.findAllByNameContainingOrderByIdDesc(name, pageRequest))
                .thenReturn(new PageImpl<>(chatRooms, pageRequest, chatRooms.size()));

        Page<ChatRoomShortDto> result = chatRoomService.findAllChatRoomsByFilterParams(name, pageRequest);

        assertEquals(chatRooms.size(), result.getContent().size());
        assertTrue(result.getContent().stream().map(ChatRoomShortDto::getName).allMatch(n -> n.contains(name)));
    }
}