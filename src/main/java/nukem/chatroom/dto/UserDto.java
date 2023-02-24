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

    public static UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .build();
    }
}
