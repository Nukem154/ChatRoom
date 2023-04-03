package nukem.chatroom.repository;

import nukem.chatroom.model.VideoStream;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StreamRepository extends JpaRepository<VideoStream, Long> {
    Optional<VideoStream> findByUserUsername(String username);

    void deleteByUserUsername(String username);
}
