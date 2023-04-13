package nukem.chatroom.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nukem.chatroom.dto.request.RegisterRequest;
import nukem.chatroom.exception.UserAlreadyExistsException;
import nukem.chatroom.exception.UserNotFoundException;
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
import java.util.List;

import static nukem.chatroom.constants.Constants.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;
    private final AWSService awsService;

    @Override
    @Transactional
    public User createUser(final RegisterRequest request) {
        validateRegisterRequest(request);
        checkIfUsernameAvailable(request.username());
        return userRepository.save(createUserFromRequest(request));
    }

    private void validateRegisterRequest(final RegisterRequest request) {
        if (StringUtils.isEmpty(request.username()) || StringUtils.isEmpty(request.password())) {
            throw new IllegalArgumentException(PASSWORD + SLASH + USERNAME + CANNOT_BE_EMPTY);
        }
    }

    private void checkIfUsernameAvailable(final String username) {
        userRepository.findByUsername(username).ifPresent((u) -> {
            throw new UserAlreadyExistsException(username);
        });
    }

    private User createUserFromRequest(final RegisterRequest request) {
        return User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .roles(Collections.singleton(Role.ROLE_USER))
                .build();
    }

    @Override
    @Transactional
    public String updateAvatar(final MultipartFile avatarImage) {
        final User user = authService.getCurrentUser();
        final String avatarUrl = awsService.uploadAvatarToAWS(avatarImage);
        final Avatar avatar = Avatar.builder()
                .url(avatarUrl)
                .user(user)
                .build();
        user.setAvatar(avatar);
        userRepository.save(user);
        return avatarUrl;
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserByUsername(final String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getUsersByUsernames(final List<String> usernames) {
        return userRepository.findByUsernameIn(usernames);
    }
}
