package nukem.chatroom.controller;

import lombok.RequiredArgsConstructor;
import nukem.chatroom.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/avatar")
    public ResponseEntity<String> updateAvatar(@RequestParam("image") MultipartFile file) {
        return ResponseEntity.ok(userService.updateAvatar(file));
    }
}
