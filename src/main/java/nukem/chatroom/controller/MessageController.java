package nukem.chatroom.controller;

import lombok.RequiredArgsConstructor;
import nukem.chatroom.dto.request.MessageRequest;
import nukem.chatroom.service.MessageService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping
    public ResponseEntity<?> getUserMessages(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "15") Integer size) {
        return ResponseEntity.ok(messageService.getUserMessages(PageRequest.of(page, size, Sort.by("date"))));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editMessage(@PathVariable Long id, @RequestBody MessageRequest messageRequest) {
        return ResponseEntity.ok(messageService.editMessage(id, messageRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMessage(@PathVariable Long id) {
        messageService.deleteMessage(id);
        return ResponseEntity.noContent().build();
    }

}
