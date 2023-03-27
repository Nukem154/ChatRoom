package nukem.chatroom.controller;

import lombok.RequiredArgsConstructor;
import nukem.chatroom.dto.UserDto;
import nukem.chatroom.service.AuthService;
import nukem.chatroom.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    @GetMapping
    public ResponseEntity<UserDto> getCurrentUser() {
        return ResponseEntity.ok(UserDto.toDto(authService.getCurrentUser()));
    }

    @PostMapping("/avatar")
    public ResponseEntity<String> updateAvatar(@RequestParam("image") MultipartFile file) {
        return ResponseEntity.ok(userService.updateAvatar(file));
    }
}
