package nukem.chatroom.repository;

import nukem.chatroom.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
    Page<Message> findAllByUserIdOrderByIdDesc(Long id, Pageable pageable);

    Page<Message> findAllByChatRoomId(Long id, Pageable pageable);
}
