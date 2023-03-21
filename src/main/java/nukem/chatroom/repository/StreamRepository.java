package nukem.chatroom.repository;

import nukem.chatroom.model.Stream;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StreamRepository extends JpaRepository<Stream, Long> {
    Optional<Stream> findByUserUsername(String username);

    void deleteByUserUsername(String username);
}
