package nukem.chatroom.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateRoomRequest(@NotBlank @Size(max = 30) String name,
                                @NotBlank @Size(max = 400) String description) {
}
