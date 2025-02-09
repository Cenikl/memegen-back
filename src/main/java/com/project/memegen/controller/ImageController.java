package com.project.memegen.controller;

import com.project.memegen.dto.ImageDto;
import com.project.memegen.mapper.ImageMapper;
import com.project.memegen.service.ImageService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
public class ImageController {

    private final ImageService imageService;
    private final ImageMapper mapper;

    @GetMapping("/images")
    public List<ImageDto> getAllUserImages(@RequestHeader("Authorization") String token) {
        String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        return imageService.userImages(jwtToken)
                .stream()
                .map(mapper::toRest)
                .toList();
    }


    @PostMapping("/image/send")
    public ImageDto uploadFile(@RequestParam("file") MultipartFile file,@RequestHeader("Authorization") String token) throws IOException {
        String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        return mapper.toRest(imageService.uploadImage(file,jwtToken,null));
    }

    @PutMapping("/image/update")
    public ImageDto updateImage(
            @RequestParam String imageUrl,
            @RequestHeader("Authorization") String token,
            @RequestParam("file") MultipartFile file) throws IOException {
        String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        imageService.deleteImage(imageUrl);
        return mapper.toRest(imageService.uploadImage(file,jwtToken,imageUrl));
    }

    @PutMapping("/image/delete")
    public ResponseEntity<Map<String, String>> deleteImage(
            @RequestHeader("Authorization") String token,
            @RequestParam String imageUrl) {
        if(token == null) throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Not authorized");
        Map<String, String> response = new HashMap<>();
        response.put("isdeleted", imageService.deleteImage(imageUrl));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/image/download")
    public ResponseEntity<byte[]> downloadImage(
            @RequestHeader("Authorization") String token,
            @RequestParam String imageUrl) {
        if(token == null) throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Not authorized");
        return imageService.downloadImage(imageUrl);
    }
}
