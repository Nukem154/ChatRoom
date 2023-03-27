package nukem.chatroom.controller;

import lombok.RequiredArgsConstructor;
import nukem.chatroom.dto.MessageDto;
import nukem.chatroom.dto.request.SendMessageRequest;
import nukem.chatroom.model.Message;
import nukem.chatroom.service.MessageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping("/messages")
    public ResponseEntity<Page<Message>> getUserMessages(@RequestParam(defaultValue = "0") Integer page,
                                                         @RequestParam(defaultValue = "15") Integer size) {
        return ResponseEntity.ok(messageService.getUserMessagesOrderByIdDesc(PageRequest.of(page, size)));
    }

    @GetMapping("/chatrooms/{chatRoomId}/messages")
    public ResponseEntity<Page<MessageDto>> getMessages(@PathVariable Long chatRoomId,
                                                        @RequestParam(defaultValue = "0") Integer page,
                                                        @RequestParam(defaultValue = "15") Integer size) {
        return ResponseEntity.ok(messageService.getMessagesByChatRoomId(chatRoomId, PageRequest.of(page, size)));
    }

    @PostMapping("/chatrooms/{chatRoomId}/messages")
    public ResponseEntity<Message> sendMessage(@PathVariable Long chatRoomId, @RequestBody SendMessageRequest message) {
        return ResponseEntity.ok(messageService.sendMessage(chatRoomId, message));
    }

    @PutMapping("/messages/{id}")
    public ResponseEntity<Message> editMessage(@PathVariable Long id, @RequestBody SendMessageRequest sendMessageRequest) {
        return ResponseEntity.ok(messageService.editMessage(id, sendMessageRequest));
    }

    @DeleteMapping("/messages/{id}")
    public ResponseEntity<HttpStatus> deleteMessage(@PathVariable Long id) {
        messageService.deleteMessage(id);
        return ResponseEntity.noContent().build();
    }

}
