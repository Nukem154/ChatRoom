package nukem.chatroom.service.impl;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import nukem.chatroom.service.AWSService;
import nukem.chatroom.service.AuthService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AWSServiceImpl implements AWSService {
    private static final String AWS_S3_BUCKET_NAME = "nukem-chatroom";
    public static final String AVATAR_SUFFIX = "_AVATAR";

    private final AmazonS3 s3Client;
    private final AuthService authService;

    @SneakyThrows
    @Override
    public String uploadAvatarToAWS(final MultipartFile multipartFile) {
        final String key = authService.getCurrentUser().getUsername() + AVATAR_SUFFIX;
        final ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(multipartFile.getContentType());
        final PutObjectRequest putObjectRequest = new PutObjectRequest(AWS_S3_BUCKET_NAME, key, multipartFile.getInputStream(), metadata);
        try {
            s3Client.putObject(putObjectRequest);
            return s3Client.getUrl(AWS_S3_BUCKET_NAME, key).toString();
        } catch (AmazonServiceException ase) {
            // handle AmazonServiceException errors
            throw new RuntimeException(ase);
        } catch (AmazonClientException ace) {
            // handle AmazonClientException errors
            throw new RuntimeException(ace);
        }
    }
}
