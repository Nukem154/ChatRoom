package nukem.chatroom.service.impl;

import nukem.chatroom.dto.request.RegisterRequest;
import nukem.chatroom.exception.UserAlreadyExistsException;
import nukem.chatroom.model.user.Role;
import nukem.chatroom.model.user.User;
import nukem.chatroom.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private final String password = "password";
    private final String username = "johndoe";

    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;


    @Test
    void createUser() {
        RegisterRequest request = new RegisterRequest(username, password);
        Mockito.when(userRepository.findByUsername(request.username())).thenReturn(Optional.empty());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        userService.createUser(request);

        Mockito.verify(userRepository, Mockito.times(1)).save(captor.capture());

        User savedUser = captor.getValue();
        assertEquals(username, savedUser.getUsername());
        assertEquals(passwordEncoder.encode(password), savedUser.getPassword());
        assertEquals(Collections.singleton(Role.ROLE_USER), savedUser.getRoles());
    }


    @Test
    void createUserWithExistingUsername() {
        RegisterRequest request = new RegisterRequest(username, password);
        Mockito.when(userRepository.findByUsername(request.username()))
                .thenReturn(Optional.of(new User(username, password, Collections.singleton(Role.ROLE_USER))));
        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(request));
        assertEquals("User with username '" + username + "' already exists", exception.getMessage());
        Mockito.verify(userRepository, Mockito.never()).save(any());
    }

    @Test
    void createUserWithEmptyUsername() {
        RegisterRequest request = new RegisterRequest("", password);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.createUser(request));
        assertEquals("Username cannot be empty", exception.getMessage());
        Mockito.verify(userRepository, Mockito.never()).save(any());
    }

    @Test
    void createUserWithEmptyPassword() {
        RegisterRequest request = new RegisterRequest(username, "");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.createUser(request));
        assertEquals("Password cannot be empty", exception.getMessage());
        Mockito.verify(userRepository, Mockito.never()).save(any());
    }

}