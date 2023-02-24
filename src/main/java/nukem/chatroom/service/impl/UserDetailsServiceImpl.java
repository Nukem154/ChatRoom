package nukem.chatroom.service.impl;

import lombok.RequiredArgsConstructor;
import nukem.chatroom.model.user.UserAuth;
import nukem.chatroom.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(UserAuth::new)
                .orElseThrow(() -> new UsernameNotFoundException("User with the specified username is not found"));
    }

}
