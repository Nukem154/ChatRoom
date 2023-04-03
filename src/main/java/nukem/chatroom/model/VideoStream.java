package nukem.chatroom.model;

import jakarta.persistence.*;
import lombok.*;
import nukem.chatroom.model.user.User;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoStream {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    private ChatRoom chatRoom;

    @OneToOne
    private User user;

    @OneToMany
    @Builder.Default
    private Set<User> viewers = new HashSet<>();
}
