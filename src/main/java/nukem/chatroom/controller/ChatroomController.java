package nukem.chatroom.controller;

import lombok.RequiredArgsConstructor;
import nukem.chatroom.dto.chatroom.ChatRoomDetailedDto;
import nukem.chatroom.dto.chatroom.ChatRoomShortDto;
import nukem.chatroom.dto.request.CreateRoomRequest;
import nukem.chatroom.model.ChatRoom;
import nukem.chatroom.service.ChatRoomService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chatrooms")
@RequiredArgsConstructor
public class ChatroomController {

    private final ChatRoomService chatRoomService;

    @GetMapping
    public ResponseEntity<Page<ChatRoomShortDto>> getChatRooms(@RequestParam(required = false) String name,
                                                               @RequestParam(defaultValue = "0") Integer page,
                                                               @RequestParam(defaultValue = "15") Integer size) {
        return ResponseEntity.ok().body(chatRoomService.findAllChatroomsByFilterParams(name, PageRequest.of(page, size)));
    }

    @PostMapping
    public ResponseEntity<ChatRoom> createChatRoom(@RequestBody CreateRoomRequest request) {
        return ResponseEntity.ok().body(chatRoomService.createChatRoom(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChatRoomDetailedDto> getChatRoomDetailedInfo(@PathVariable Long id) {
        return ResponseEntity.ok().body(chatRoomService.getChatRoomInfo(id));
    }

//    @GetMapping("/{id}/users")
//    public ResponseEntity<Set<String>> getActiveUsers(@PathVariable Long id) {
//        return ResponseEntity.ok().body(chatRoomService.getActiveUsersInRoom(id));
//    }
//
//    @PostMapping("/{chatRoomId}/join")
//    public ResponseEntity<HttpStatus> joinChatRoom(@PathVariable Long chatRoomId) {
//        chatRoomService.joinChatRoom(chatRoomId);
//        return ResponseEntity.noContent().build();
//    }
//
//    @PostMapping("/{chatRoomId}/leave")
//    public ResponseEntity<HttpStatus> leaveChatRoom(@PathVariable Long chatRoomId) {
//        chatRoomService.leaveChatRoom(chatRoomId);
//        return ResponseEntity.noContent().build();
//    }
}
