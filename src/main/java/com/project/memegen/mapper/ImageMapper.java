package com.project.memegen.mapper;

import com.project.memegen.dto.ImageDto;
import com.project.memegen.entity.Image;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
public class ImageMapper {
    public ImageDto toRest(Image image){
        return ImageDto.builder()
                .name(image.getName())
                .url(image.getUrl())
                .imageOwner(image.getUser().getUsername())
                .build();
    }
}
