package nukem.chatroom.dto.chatroom;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import nukem.chatroom.model.ChatRoom;

@Getter
@Setter
@Builder
public class ChatRoomShortDto {
    private Long id;
    private String name;
    private String description;

    public static ChatRoomShortDto toDto(final ChatRoom chatRoom) {
        return ChatRoomShortDto.builder()
                .id(chatRoom.getId())
                .name(chatRoom.getName())
                .description(chatRoom.getDescription())
                .build();
    }
}
