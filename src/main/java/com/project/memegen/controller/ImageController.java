package com.project.memegen.controller;


import com.project.memegen.dto.ImageDto;
import com.project.memegen.entity.Image;
import com.project.memegen.mapper.ImageMapper;
import com.project.memegen.service.ImageService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
public class ImageController {

    private final ImageService imageService;
    private final ImageMapper mapper;

    @GetMapping("/images")
    public List<ImageDto> getAllUserImages(@RequestParam String token){
        return imageService.userImages(token)
                .stream()
                .map(mapper::toRest)
                .toList();
    }

    @PostMapping("/image/send")
    public ImageDto uploadFile(@RequestParam("file") MultipartFile file,@RequestParam String token) throws IOException {
        return mapper.toRest(imageService.uploadImage(file,token,null));
    }

    @PutMapping("/image/update")
    public ImageDto updateImage(
            @RequestParam String imageUrl,
            @RequestParam String token,
            @RequestParam("file") MultipartFile file) throws IOException {
        imageService.deleteImage(imageUrl);
        return mapper.toRest(imageService.uploadImage(file,token,imageUrl));
    }

    @PutMapping("/image/delete")
    public String deleteImage(@RequestParam String imageUrl){
        return imageService.deleteImage(imageUrl);
    }

    @GetMapping("/image/download")
    public ResponseEntity<byte[]> downloadImage(@RequestParam String imageUrl) {
        return imageService.downloadImage(imageUrl);
    }
}
