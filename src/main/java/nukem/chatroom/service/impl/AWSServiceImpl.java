package nukem.chatroom.service.impl;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import nukem.chatroom.service.AWSService;
import nukem.chatroom.service.AuthService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URL;

@Service
@RequiredArgsConstructor
public class AWSServiceImpl implements AWSService {

    private final AmazonS3Client s3Client;
    private final AuthService authService;

    @Override
    public String uploadToAWS(final MultipartFile multipartFile) {
        final String imageBucket = "images";
        final String key = authService.getCurrentUser().getUsername() + "_AVATAR";
        final File file = convertMultipartFileToFile(multipartFile);

        try {
            s3Client.putObject(new PutObjectRequest(imageBucket, key, file));
            final URL url = s3Client.getUrl(imageBucket, key);
            return url.toString();
        } catch (AmazonServiceException ase) {
            // handle AmazonServiceException errors
            throw new RuntimeException(ase);
        } catch (AmazonClientException ace) {
            // handle AmazonClientException errors
            throw new RuntimeException(ace);
        } finally {
            file.delete();
        }
    }

    private File convertMultipartFileToFile(final MultipartFile multipartFile) {
        final File file = new File("temp");
        try {
            multipartFile.transferTo(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return file;
    }
}
