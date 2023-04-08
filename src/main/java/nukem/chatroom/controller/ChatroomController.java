package nukem.chatroom.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nukem.chatroom.dto.chatroom.ChatRoomDetailedDto;
import nukem.chatroom.dto.chatroom.ChatRoomShortDto;
import nukem.chatroom.dto.request.CreateRoomRequest;
import nukem.chatroom.service.ChatRoomService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
        final PageRequest pageRequest = PageRequest.of(page, size, Sort.by("id").descending());
        return ResponseEntity.ok().body(chatRoomService.findAllChatRoomsByFilterParams(name, pageRequest));
    }

    @PostMapping
    public ResponseEntity<ChatRoomDetailedDto> createChatRoom(@RequestBody @Valid CreateRoomRequest request) {
        return ResponseEntity.ok().body(chatRoomService.createChatRoom(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChatRoomDetailedDto> getChatRoomDetailedInfo(@PathVariable Long id) {
        return ResponseEntity.ok().body(chatRoomService.getChatRoomInfo(id));
    }
}
