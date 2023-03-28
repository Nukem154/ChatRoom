package nukem.chatroom.service;

import org.springframework.security.core.Authentication;

/**
 * This service generates a JWT token for a given {@link Authentication} object.
 */
public interface TokenService {
    /**
     * Generates a JWT token based on the provided authentication object.
     *
     * @param authentication The authentication object for which a JWT token is to be generated.
     * @return A JWT token that can be used for authentication in subsequent requests.
     */
    String generateToken(Authentication authentication);
}
