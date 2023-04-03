package nukem.chatroom.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import nukem.chatroom.model.VideoStream;
import nukem.chatroom.model.user.User;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
public class VideoStreamDto {
    private Long id;
    private String streamer;
    private Set<String> viewers;

    public static VideoStreamDto toDto(final VideoStream videoStream) {
        return VideoStreamDto.builder()
                .id(videoStream.getId())
                .streamer(videoStream.getUser().getUsername())
                .viewers(videoStream.getViewers().stream().map(User::getUsername).collect(Collectors.toSet()))
                .build();
    }
}
