package nukem.chatroom.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nukem.chatroom.dto.request.RegisterRequest;
import nukem.chatroom.exception.UserAlreadyExistsException;
import nukem.chatroom.model.user.Role;
import nukem.chatroom.model.user.User;
import nukem.chatroom.repository.UserRepository;
import nukem.chatroom.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    public static final String CANNOT_BE_EMPTY = "cannot be empty";
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User createUser(RegisterRequest request) {
        final String username = request.username();
        final String password = request.password();
        if (StringUtils.isEmpty(username)) {
            throw new IllegalArgumentException("Username " + CANNOT_BE_EMPTY);
        }
        if (StringUtils.isEmpty(password)) {
            throw new IllegalArgumentException("Password " + CANNOT_BE_EMPTY);
        }
        userRepository.findByUsername(username).ifPresent((u) -> {
            throw new UserAlreadyExistsException(username);
        });
        final User user = new User(username, passwordEncoder.encode(password), Collections.singleton(Role.ROLE_USER));

        return userRepository.save(user);
    }
}
