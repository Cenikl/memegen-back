package com.project.memegen.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class NameUtil {
        public static String sanitizeFileName(MultipartFile file) {
            String originalFileName = file.getOriginalFilename();

            if (originalFileName == null) {
                throw new IllegalArgumentException("File name cannot be null");
            }
            int dotIndex = originalFileName.lastIndexOf('.');
            String baseName;
            String extension = "";

            if (dotIndex != -1) {
                baseName = originalFileName.substring(0, dotIndex);
                extension = originalFileName.substring(dotIndex); // includes the dot
            } else {
                baseName = originalFileName;
            }
            String sanitizedBaseName = baseName.replaceAll("[^a-zA-Z0-9]", "");
            return sanitizedBaseName + extension;
        }
}
