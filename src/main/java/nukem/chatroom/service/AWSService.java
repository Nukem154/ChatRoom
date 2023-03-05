package nukem.chatroom.service;

import org.springframework.web.multipart.MultipartFile;

public interface AWSService {
    String uploadToAWS(MultipartFile file);
}
