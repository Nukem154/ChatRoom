package nukem.chatroom.controller;

import lombok.RequiredArgsConstructor;
import nukem.chatroom.dto.request.CreateRoomRequest;
import nukem.chatroom.dto.request.MessageRequest;
import nukem.chatroom.service.ChatRoomService;
import nukem.chatroom.service.MessageService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chatrooms")
@RequiredArgsConstructor
public class ChatroomController {

    private final ChatRoomService chatRoomService;
    private final MessageService messageService;

    @GetMapping
    public ResponseEntity<?> getChatRooms() {
        return ResponseEntity.ok().body(chatRoomService.getChatRooms());
    }

    @PostMapping
    public ResponseEntity<?> createChatRoom(@RequestBody CreateRoomRequest request) {
        return ResponseEntity.ok().body(chatRoomService.createChatRoom(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getChatRoomDetailedInfo(@PathVariable Long id) {
        return ResponseEntity.ok().body(chatRoomService.getChatRoomInfo(id));
    }

    @PostMapping("/{chatRoomId}/join")
    public ResponseEntity<?> joinChatRoom(@PathVariable Long chatRoomId) {
        chatRoomService.joinChatRoom(chatRoomId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{chatRoomId}/leave")
    public ResponseEntity<?> leaveChatRoom(@PathVariable Long chatRoomId) {
        chatRoomService.leaveChatRoom(chatRoomId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{chatRoomId}/messages")
    public ResponseEntity<?> getMessages(@PathVariable Long chatRoomId, @RequestParam(defaultValue = "0") Integer page,
                                         @RequestParam(defaultValue = "15") Integer size) {
        return ResponseEntity.ok(messageService.getMessagesByChatRoomId(chatRoomId, PageRequest.of(page, size)));
    }

    @PostMapping("/{chatRoomId}/messages")
    public ResponseEntity<?> sendMessage(@PathVariable Long chatRoomId, @RequestBody MessageRequest message) {
        return ResponseEntity.ok(messageService.sendMessage(chatRoomId, message));
    }
}
