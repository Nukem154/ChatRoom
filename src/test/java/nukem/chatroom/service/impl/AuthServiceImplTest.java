package nukem.chatroom.service.impl;

import nukem.chatroom.dto.request.LoginRequest;
import nukem.chatroom.model.user.User;
import nukem.chatroom.repository.UserRepository;
import nukem.chatroom.service.TokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private TokenService tokenService;
    @InjectMocks
    private AuthServiceImpl authService;

    private final String username = "testUser";
    private final String password = "password";

    @Test
    void getCurrentUserTest() {
        final User user = User.builder()
                .username(username)
                .password(password)
                .build();
        final Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        final User currentUser = authService.getCurrentUser();

        assertEquals(user, currentUser);
    }

    @Test
    void authenticateUserTest() {
        final String expectedToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0VXNlciIsImlhdCI6MTYxNjIwMzY2MCwiZXhwIjoxNjE2MjA3MjYwfQ.HARW6K8XdvTcG6z-RcVq3ncj8cbv7scO6OJU6FYsU_E";
        final Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);
        final LoginRequest loginRequest = new LoginRequest(username, password);

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(tokenService.generateToken(authentication)).thenReturn(expectedToken);

        final String token = authService.authenticateUser(loginRequest);

        assertEquals(expectedToken, token);
    }

}