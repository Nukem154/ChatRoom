package nukem.chatroom.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * This service provides methods to interact with Amazon Web Services (AWS), specifically for uploading files to the AWS S3 bucket.
 */
public interface AWSService {
    /**
     * Uploads a file to the AWS S3 bucket.
     *
     * @param file The file to upload.
     * @return A string representing the URL of the uploaded file.
     */
    String uploadToAWS(MultipartFile file);
}
