package nukem.chatroom.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import nukem.chatroom.model.user.User;

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
//                .avatarUrl(user.getAvatar() != null ? user.getAvatar().getUrl() : null)
                .avatarUrl("https://upload.wikimedia.org/wikipedia/commons/9/96/Dora_%282020-12-22%29.jpg")
                .build();
    }
}
