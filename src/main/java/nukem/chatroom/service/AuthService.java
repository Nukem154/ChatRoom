package nukem.chatroom.service;

import nukem.chatroom.dto.request.LoginRequest;
import nukem.chatroom.model.user.User;

/**
 * This service provides methods for authenticating users and getting the current user.
 */
public interface AuthService {
    /**
     * Retrieves the currently authenticated user.
     *
     * @return The user object for the currently authenticated user.
     */
    User getCurrentUser();

    /**
     * Authenticates a user using the provided login request object.
     *
     * @param loginRequest The login request object containing the user's credentials.
     * @return A JWT token that can be used for authentication in subsequent requests.
     */
    String authenticateUser(LoginRequest loginRequest);
}
