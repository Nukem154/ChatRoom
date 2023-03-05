package nukem.chatroom.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import nukem.chatroom.model.user.Avatar;
import nukem.chatroom.model.user.User;

import java.util.Optional;

@Getter
@Setter
@Builder
public class UserDto {
    private Long id;
    private String username;
    private String avatarUrl;

    public static UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .avatarUrl(user.getAvatar() != null ? user.getAvatar().getUrl() : null)
                .build();
    }
}
