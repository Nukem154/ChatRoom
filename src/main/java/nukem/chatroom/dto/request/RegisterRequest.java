package nukem.chatroom.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(@NotBlank @Size(min = 5, max = 15) String username,
                              @NotBlank @Size(min = 5, max = 15) String password) {
}
