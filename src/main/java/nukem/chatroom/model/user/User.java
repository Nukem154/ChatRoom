package nukem.chatroom.model.user;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import nukem.chatroom.model.ChatRoom;
import nukem.chatroom.model.Message;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "avatar_id")
    private Avatar avatar;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    @JsonManagedReference
    @ManyToMany(mappedBy = "users")
    private Set<ChatRoom> chatRooms = new HashSet<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Message> messages = new ArrayList<>();

    public User() {
    }

    public User(String username, String password, Set<Role> roles) {
        this.username = username;
        this.password = password;
        this.roles = roles;
    }

}
