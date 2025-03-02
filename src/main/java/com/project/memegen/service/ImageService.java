package com.project.memegen.service;

import com.project.memegen.entity.Image;
import com.project.memegen.repository.UserRepository;
import com.project.memegen.utils.JwtUtil;
import com.project.memegen.utils.NameUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.project.memegen.repository.ImageRepository;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class ImageService {

    @Value("${project_url}")
    private String SUPABASE_URL;

    @Value("${project_key}")
    private String SUPABASE_API_KEY;

    @Value("${bucket_name}")
    private String BUCKET_NAME;

    private final ImageRepository imageRepository;

    private final UserRepository userRepository;

    private final WebClient webClient = WebClient.create();

    public ImageService(ImageRepository imageRepository, UserRepository userRepository) {
        this.imageRepository = imageRepository;
        this.userRepository = userRepository;
    }

    public Image uploadImage(MultipartFile file, String token, String imageUrl) throws IOException {
        byte[] fileBytes = file.getBytes();
        String folder = "uploads/";
        String originalFilename = UUID.randomUUID() + NameUtil.sanitizeFileName(file);
        String encodedFilename = URLEncoder.encode(originalFilename, StandardCharsets.UTF_8.toString());
        String objectPath = folder + encodedFilename;
        String uploadUrl = String.format("%s/storage/v1/object/%s/%s?upsert=true",
                SUPABASE_URL, BUCKET_NAME, objectPath);

        HttpHeaders headers = new HttpHeaders();
        String contentType = file.getContentType() != null ? file.getContentType() : MediaType.APPLICATION_OCTET_STREAM_VALUE;
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentLength(fileBytes.length);
        headers.set("apikey", SUPABASE_API_KEY);
        headers.set("Authorization", "Bearer "+SUPABASE_API_KEY);

        HttpEntity<byte[]> requestEntity = new HttpEntity<>(fileBytes, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(uploadUrl, HttpMethod.PUT, requestEntity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            String url = SUPABASE_URL + "/storage/v1/object/public/" + BUCKET_NAME +"/"+ objectPath;
            Optional<Image> optionalImage = imageRepository.findByUrl(imageUrl);
            if(optionalImage.isPresent()){
                Image image = optionalImage.get();
                image.setUrl(url);
                image.setUpdatedAt(Timestamp.from(Instant.now()));
                return imageRepository.save(image);
            }
            Image newImage = Image.builder()
                    .url(url)
                    .name(NameUtil.sanitizeFileName(file))
                    .user(userRepository.findByUsername(JwtUtil.extractUsername(token)).get())
                    .createdAt(Timestamp.from(Instant.now()))
                    .updatedAt(Timestamp.from(Instant.now()))
                    .build();
            return imageRepository.save(newImage);
        } else {
            throw new RuntimeException("Failed to upload image to Supabase");
        }
    }

    @Transactional
    public String deleteImage(String imageUrl) {
        String cleanedUrl = imageUrl.replace("/public/", "/");
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + SUPABASE_API_KEY);
        headers.set("apikey", SUPABASE_API_KEY);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(cleanedUrl, HttpMethod.DELETE, entity, String.class);
        imageRepository.deleteByUrl(imageUrl);

        if (response.getStatusCode().is2xxSuccessful()) {
            return "File deleted successfully.";
        } else {
            return "Failed to delete file: " + response.getBody();
        }
    }

    public ResponseEntity<byte[]> downloadImage(String imageUrl) {
        String filename = imageRepository.findByUrl(imageUrl).get().getUrl();
        String cleanedUrl = imageUrl.replace("/public/", "/");
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + SUPABASE_API_KEY);
        headers.set("apikey", SUPABASE_API_KEY);

        ResponseEntity<byte[]> response = restTemplate.exchange(
                cleanedUrl, HttpMethod.GET, new HttpEntity<>(headers), byte[].class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(MediaType.IMAGE_JPEG);
            responseHeaders.setContentDisposition(ContentDisposition.attachment().filename(filename).build());

            return new ResponseEntity<>(response.getBody(), responseHeaders, HttpStatus.OK);
        } else {
            return ResponseEntity.status(response.getStatusCode()).body(null);
        }
    }

    public List<Image> userImages(String token){
        return imageRepository.findByUser_Username(JwtUtil.extractUsername(token));
    }

}
