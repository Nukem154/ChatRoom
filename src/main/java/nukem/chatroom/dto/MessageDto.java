package nukem.chatroom.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import nukem.chatroom.model.Message;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class MessageDto {
    private Long id;
    private String content;
    private LocalDateTime date;
    private UserDto user;

    public static MessageDto toDto(Message message) {
        return MessageDto.builder()
                .id(message.getId())
                .content(message.getContent())
                .date(message.getDate())
                .user(UserDto.toDto(message.getUser()))
                .build();
    }
}

