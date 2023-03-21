package nukem.chatroom.controller;

import lombok.RequiredArgsConstructor;
import nukem.chatroom.service.AuthService;
import nukem.chatroom.service.StreamService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class StreamController {

    private final StreamService streamService;
    private final AuthService authService;

    @GetMapping("/chatrooms/{chatRoomId}/stream/start")
    public ResponseEntity<?> startStream(@PathVariable Long chatRoomId) {
        streamService.startStream(chatRoomId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/stream/end")
    public ResponseEntity<?> endStream() {
        streamService.endStream(authService.getCurrentUser().getUsername());
        return ResponseEntity.ok().build();
    }
}
