package nukem.chatroom.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nukem.chatroom.dto.request.RegisterRequest;
import nukem.chatroom.exception.UserAlreadyExistsException;
import nukem.chatroom.model.user.Avatar;
import nukem.chatroom.model.user.Role;
import nukem.chatroom.model.user.User;
import nukem.chatroom.repository.UserRepository;
import nukem.chatroom.service.AWSService;
import nukem.chatroom.service.AuthService;
import nukem.chatroom.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    public static final String CANNOT_BE_EMPTY = "cannot be empty";
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;
    private final AWSService awsService;

    @Override
    @Transactional
    public User createUser(final RegisterRequest request) {
        final String username = request.username();
        final String password = request.password();

        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            throw new IllegalArgumentException("Username/Password " + CANNOT_BE_EMPTY);
        }

        userRepository.findByUsername(username).ifPresent((u) -> {
            throw new UserAlreadyExistsException(username);
        });
        final User user = new User(username, passwordEncoder.encode(password), Collections.singleton(Role.ROLE_USER));

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public String updateAvatar(final MultipartFile file) {
        final User user = authService.getCurrentUser();
        final Avatar avatar = new Avatar();
        final String avatarUrl = awsService.uploadToAWS(file);
        avatar.setUrl(avatarUrl);
        avatar.setUser(user);
        user.setAvatar(avatar);
        userRepository.save(user);
        return avatarUrl;
    }
}
