package nukem.chatroom.dto.chatroom;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import nukem.chatroom.dto.videostream.VideoStreamDto;
import nukem.chatroom.model.user.User;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatRoomMemberDto {

    private Long id;
    private String username;
    private String avatarUrl;
    private VideoStreamDto stream;

    public static ChatRoomMemberDto toDto(User user) {
        return ChatRoomMemberDto.builder()
                .id(user.getId())
                .username(user.getUsername())
//                .avatarUrl(user.getAvatar() != null ? user.getAvatar().getUrl() : null)
                .avatarUrl("https://upload.wikimedia.org/wikipedia/commons/9/96/Dora_%282020-12-22%29.jpg")
                .build();
    }
}
