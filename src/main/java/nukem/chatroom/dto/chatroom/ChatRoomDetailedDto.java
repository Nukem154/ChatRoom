package nukem.chatroom.dto.chatroom;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import nukem.chatroom.dto.UserDto;
import nukem.chatroom.model.ChatRoom;

import java.util.List;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatRoomDetailedDto {
    private Long id;
    private String name;
    private String description;
    private String owner;
    private List<UserDto> activeUsers;

    public static ChatRoomDetailedDto toDto(final ChatRoom chatRoom) {
        return ChatRoomDetailedDto.builder()
                .id(chatRoom.getId())
                .name(chatRoom.getName())
                .description(chatRoom.getDescription())
                .owner(chatRoom.getOwner().getUsername())
                .build();
    }
}

