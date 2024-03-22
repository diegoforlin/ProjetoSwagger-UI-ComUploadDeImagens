package adopet.api.controller;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;

@Slf4j
@Controller
@RequestMapping("pets")
@Configuration
public class FileController {
    @Autowired
    @Value("${server.upload.dir}")
    private String uploadDir;

    @Autowired
    @Bean
    @PostMapping(name = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadFile(@RequestPart("file") MultipartFile file)
            throws IOException {
        switch (Objects.requireNonNull(file.getContentType())) {
            case MediaType.IMAGE_GIF_VALUE:
            case MediaType.IMAGE_JPEG_VALUE:
            case MediaType.IMAGE_PNG_VALUE:
                processImageUpload(file);
                break;
            default:
                log.error("Unsupported filetype: {}", file.getContentType());
                throw new UnsupportedMediaTypeStatusException(
                        String.format("Unsupported filetype: %s", file.getContentType()));
        }

        // Send a response code to the client indicating the Spring file upload was successful
        return ResponseEntity.ok(
                String.format("File uploaded successfully: %s", file.getOriginalFilename()));
    }

    // Service method...
    private void processImageUpload(MultipartFile file) throws IOException {
        File destFile = Paths.get(uploadDir, file.getOriginalFilename()).toFile();
        file.transferTo(destFile);
        log.info("Uploaded: {}", destFile);
    }
}