package nukem.chatroom.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import nukem.chatroom.model.user.User;

@Entity
@Getter
@Setter
public class Stream {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    private ChatRoom chatRoom;

    @OneToOne
    private User user;
}
