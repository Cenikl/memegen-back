package com.project.memegen.repository;

import com.project.memegen.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ImageRepository extends JpaRepository<Image, UUID> {
    List<Image> findByUser_Username(@NonNull String username);

    Optional<Image> findByUrl(String url);
}
