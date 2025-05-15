package com.iss.eventorium.shared.services;

import com.iss.eventorium.shared.dtos.ImageResponseDto;
import com.iss.eventorium.shared.exceptions.ImageNotFoundException;
import com.iss.eventorium.shared.models.ImagePath;
import com.iss.eventorium.shared.utils.ImageHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Service
public class ImageService {

    @Value("${image-path}")
    private String imagePath;

    public void uploadImage(String uploadDir, String fileName, MultipartFile multipartFile) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        try (InputStream inputStream = multipartFile.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IOException("Could not upload image file: " + fileName, e);
        }
    }

    public String getImageContentType(String uploadDir, String fileName) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        Path filePath = uploadPath.resolve(fileName);
        return Files.probeContentType(filePath);
    }

    public List<ImageResponseDto> getImages(String imageDir, Long id, Function<Long, ? extends ImageHolder> entityFetcher) {
        ImageHolder entity = entityFetcher.apply(id);
        List<ImageResponseDto> images = new ArrayList<>();

        for (ImagePath path : entity.getImagePaths()) {
            byte[] image = getImage(imageDir, id, path);
            images.add(new ImageResponseDto(path.getId(), image, path.getContentType()));
        }

        return images;
    }

    public byte[] getImage(String imageDir, Long id, ImagePath path) {
        String uploadDir = StringUtils.cleanPath(imagePath + imageDir + "/" + id + "/");
        File file = new File(uploadDir, path.getPath());

        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            throw new ImageNotFoundException("Fail to load image");
        }
    }
}
