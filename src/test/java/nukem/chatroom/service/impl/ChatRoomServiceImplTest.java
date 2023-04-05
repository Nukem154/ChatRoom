package nukem.chatroom.service.impl;

import nukem.chatroom.dto.chatroom.ChatRoomDetailedDto;
import nukem.chatroom.dto.chatroom.ChatRoomMemberDto;
import nukem.chatroom.dto.chatroom.ChatRoomShortDto;
import nukem.chatroom.dto.request.CreateRoomRequest;
import nukem.chatroom.model.ChatRoom;
import nukem.chatroom.model.user.User;
import nukem.chatroom.repository.ChatRoomRepository;
import nukem.chatroom.service.AuthService;
import nukem.chatroom.service.UserService;
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
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatRoomServiceImplTest {
    @Mock
    private ChatRoomRepository chatRoomRepository;
    @Mock
    private AuthService authService;
    @Mock
    private UserService userService;
    @InjectMocks
    @Spy
    private ChatRoomServiceImpl chatRoomService;

    @Test
    void createChatRoomTest() {
        final CreateRoomRequest request = new CreateRoomRequest("Test Room", "This is a test room");
        final User currentUser = User.builder()
                .username("testuser")
                .build();
        final ChatRoom chatRoom = ChatRoom.builder()
                .name(request.name())
                .description(request.description())
                .owner(currentUser)
                .build();

        when(authService.getCurrentUser()).thenReturn(currentUser);
        when(chatRoomRepository.save(any(ChatRoom.class))).thenReturn(chatRoom);

        final ChatRoomDetailedDto result = chatRoomService.createChatRoom(request);

        assertEquals(request.name(), result.getName());
        assertEquals(request.description(), result.getDescription());
        assertEquals(currentUser.getUsername(), result.getOwner());
    }

    @Test
    void getChatRoomInfoTest() {
        final Long roomId = 1L;
        final User owner = User.builder()
                .username("testuser")
                .build();
        final ChatRoom chatRoom = ChatRoom.builder()
                .id(roomId)
                .name("Test Room")
                .description("This is a test room")
                .videoStreams(new HashSet<>())
                .owner(owner)
                .build();
        final Set<String> activeUsers = Set.of("user1", "user2");

        when(chatRoomRepository.findById(roomId)).thenReturn(Optional.of(chatRoom));
        doReturn(activeUsers).when(chatRoomService).getActiveUsersInRoom(roomId);
        for (String username : activeUsers) {
            when(userService.getUserByUsername(username)).thenReturn(User.builder().username(username).build());
        }

        final ChatRoomDetailedDto result = chatRoomService.getChatRoomInfo(roomId);

        assertEquals(roomId, result.getId());
        assertEquals(chatRoom.getName(), result.getName());
        assertEquals(chatRoom.getDescription(), result.getDescription());
        assertEquals(owner.getUsername(), result.getOwner());
        assertEquals(activeUsers.size(), result.getActiveUsers().size());
        assertTrue(result.getActiveUsers().stream().map(ChatRoomMemberDto::getUsername).allMatch(activeUsers::contains));
    }

    @Test
    void findAllChatRoomsByFilterParamsTest() {
        final String name = "Test Room";
        final Pageable pageRequest = PageRequest.of(0, 10);
        final List<ChatRoom> chatRooms = List.of(
                ChatRoom.builder().id(1L).name("Test Room 1").description("This is test room 1").build(),
                ChatRoom.builder().id(2L).name("Test Room 2").description("This is test room 2").build()
        );

        when(chatRoomRepository.findAllByNameContainingOrderByIdDesc(name, pageRequest))
                .thenReturn(new PageImpl<>(chatRooms, pageRequest, chatRooms.size()));

        final Page<ChatRoomShortDto> result = chatRoomService.findAllChatRoomsByFilterParams(name, pageRequest);

        assertEquals(chatRooms.size(), result.getContent().size());
        assertTrue(result.getContent().stream().map(ChatRoomShortDto::getName).allMatch(n -> n.contains(name)));
    }
}