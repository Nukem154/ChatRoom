package nukem.chatroom.controller;

import lombok.RequiredArgsConstructor;
import nukem.chatroom.dto.VideoStreamDto;
import nukem.chatroom.service.AuthService;
import nukem.chatroom.service.VideoStreamService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class VideoStreamController {

    private final VideoStreamService videoStreamService;
    private final AuthService authService;

    @PostMapping("/chatrooms/{chatRoomId}/stream")
    public ResponseEntity<VideoStreamDto> startStream(@PathVariable Long chatRoomId) {
        return ResponseEntity.ok(videoStreamService.startStream(chatRoomId));
    }

    @DeleteMapping("/stream")
    public ResponseEntity<HttpStatus> endStream() {
        videoStreamService.endStreamByStreamerUsername(authService.getCurrentUser().getUsername());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/stream/{streamId}/watch")
    public ResponseEntity<VideoStreamDto> watchStream(@PathVariable Long streamId) {
        return ResponseEntity.ok(videoStreamService.watchStream(streamId));
    }

    @DeleteMapping("/stream/{streamId}/watch")
    public ResponseEntity<HttpStatus> stopWatchingStream(@PathVariable Long streamId) {
        videoStreamService.stopWatchingStream(streamId);
        return ResponseEntity.noContent().build();
    }
}
