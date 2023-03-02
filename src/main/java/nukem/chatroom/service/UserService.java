package nukem.chatroom.service;

import nukem.chatroom.dto.request.RegisterRequest;
import nukem.chatroom.model.user.User;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    User createUser(RegisterRequest user);

    String updateAvatar(MultipartFile file);
}
