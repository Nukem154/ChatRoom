package nukem.chatroom.service.impl;

import nukem.chatroom.dto.request.RegisterRequest;
import nukem.chatroom.exception.UserAlreadyExistsException;
import nukem.chatroom.model.user.Role;
import nukem.chatroom.model.user.User;
import nukem.chatroom.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static nukem.chatroom.constants.Constants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private final String username = "testuser";
    private final String password = "testpassword";

    @Test
    void createUser_shouldCreateUserIfUsernameAndPasswordAreNotEmpty() {
        final String hashedPassword = "hashedPassword";
        final RegisterRequest request = new RegisterRequest(username, password);
        final User user = User.builder()
                .username(username)
                .password(hashedPassword)
                .roles(Collections.singleton(Role.ROLE_USER))
                .build();

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(userRepository.save(any())).thenReturn(user);

        final User result = userService.createUser(request);

        verify(passwordEncoder).encode(password);
        assertEquals(username, result.getUsername());
        assertEquals(hashedPassword, result.getPassword());
        assertEquals(Collections.singleton(Role.ROLE_USER), result.getRoles());
    }

    @Test
    void createUser_shouldThrowExceptionIfUsernameIsEmpty() {
        final String username = "";
        final RegisterRequest request = new RegisterRequest(username, password);

        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.createUser(request));
        assertEquals(PASSWORD + SLASH + USERNAME + CANNOT_BE_EMPTY, exception.getMessage());
    }

    @Test
    void createUser_shouldThrowExceptionIfPasswordIsEmpty() {
        final String password = "";
        final RegisterRequest request = new RegisterRequest(username, password);

        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.createUser(request));
        assertEquals(PASSWORD + SLASH + USERNAME + CANNOT_BE_EMPTY, exception.getMessage());
    }

    @Test
    void createUser_shouldThrowExceptionIfUserAlreadyExists() {

        final RegisterRequest request = new RegisterRequest(username, password);

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(User.builder().build()));

        final UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(request));
        assertEquals("User with username 'testuser' already exists", exception.getMessage());
    }
}