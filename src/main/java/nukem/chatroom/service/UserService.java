package nukem.chatroom.service;

import nukem.chatroom.dto.request.RegisterRequest;
import nukem.chatroom.exception.UserAlreadyExistsException;
import nukem.chatroom.model.user.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * This service provides methods for managing user-related functionality.
 */
public interface UserService {
    /**
     * Creates a new user with the specified registration details.
     *
     * @param user the registration details of the user
     * @return the newly created user object
     * @throws IllegalArgumentException   if the username or password is empty
     * @throws UserAlreadyExistsException if a user with the same username already exists
     */
    User createUser(RegisterRequest user);

    /**
     * Updates the avatar for the current user.
     *
     * @param file the new avatar image file
     * @return the URL of the updated avatar image
     */
    String updateAvatar(MultipartFile file);

    User getUserByUsername(String username);

    List<User> getUsersByUsernames(List<String> usernames);
}
