package nukem.chatroom.dto.chatroom;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import nukem.chatroom.dto.UserDto;
import nukem.chatroom.model.ChatRoom;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
public class ChatRoomDetailedDto {
    private Long id;
    private String name;
    private String description;
    private Set<UserDto> users;

    public static ChatRoomDetailedDto toDto(final ChatRoom chatRoom) {
        return ChatRoomDetailedDto.builder()
                .id(chatRoom.getId())
                .name(chatRoom.getName())
                .description(chatRoom.getDescription())
                .users(chatRoom.getUsers().stream().map(UserDto::toDto).collect(Collectors.toSet()))
                .build();
    }
}

