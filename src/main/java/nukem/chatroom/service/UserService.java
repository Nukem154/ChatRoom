package nukem.chatroom.service;

import nukem.chatroom.dto.request.RegisterRequest;
import nukem.chatroom.model.user.User;

public interface UserService {
    User createUser(RegisterRequest user);
}
