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

import java.net.URL;

@Service
@RequiredArgsConstructor
public class AWSServiceImpl implements AWSService {

    private final AmazonS3 s3Client;
    private final AuthService authService;

    @SneakyThrows
    @Override
    public String uploadToAWS(final MultipartFile multipartFile) {
        final String imageBucket = "nukem-chatroom";
        final String key = authService.getCurrentUser().getUsername() + "_AVATAR";
        final ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(multipartFile.getContentType());
        final PutObjectRequest putObjectRequest = new PutObjectRequest(imageBucket, key, multipartFile.getInputStream(), metadata);
        try {
            s3Client.putObject(putObjectRequest);
            final URL url = s3Client.getUrl(imageBucket, key);
            return url.toString();
        } catch (AmazonServiceException ase) {
            // handle AmazonServiceException errors
            throw new RuntimeException(ase);
        } catch (AmazonClientException ace) {
            // handle AmazonClientException errors
            throw new RuntimeException(ace);
        }
    }
}
