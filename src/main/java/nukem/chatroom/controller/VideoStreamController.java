package nukem.chatroom.controller;

import lombok.RequiredArgsConstructor;
import nukem.chatroom.dto.videostream.VideoStreamDto;
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

    @PostMapping("/stream/{streamerUsername}/watch")
    public ResponseEntity<VideoStreamDto> watchStream(@PathVariable String streamerUsername) {
        return ResponseEntity.ok(videoStreamService.watchStream(streamerUsername));
    }

    @DeleteMapping("/stream/{streamerUsername}/watch")
    public ResponseEntity<HttpStatus> stopWatchingStream(@PathVariable String streamerUsername) {
        videoStreamService.stopWatchingStream(streamerUsername);
        return ResponseEntity.noContent().build();
    }
}
