package com.iss.eventorium.event.services;

import com.iss.eventorium.category.models.Category;
import com.iss.eventorium.category.services.CategoryService;
import com.iss.eventorium.event.dtos.eventtype.EventTypeRequestDto;
import com.iss.eventorium.event.mappers.EventTypeMapper;
import com.iss.eventorium.event.dtos.eventtype.EventTypeResponseDto;
import com.iss.eventorium.event.models.EventType;
import com.iss.eventorium.event.repositories.EventTypeRepository;
import com.iss.eventorium.shared.exceptions.ImageNotFoundException;
import com.iss.eventorium.shared.exceptions.ImageUploadException;
import com.iss.eventorium.shared.models.ImagePath;
import com.iss.eventorium.shared.services.ImageService;
import com.iss.eventorium.user.models.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventTypeService {

    private final EventTypeRepository repository;
    private final CategoryService categoryService;
    private final ImageService imageService;

    private final EventTypeMapper mapper;

    private static final String IMG_DIR_NAME = "eventTypes";

    public EventType find(Long id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Event type not found"));
    }

    public List<EventTypeResponseDto> getEventTypes() {
        return repository.findByDeletedFalse().stream().map(mapper::toResponse).toList();
    }

    public EventTypeResponseDto getEventType(Long id) {
        return mapper.toResponse(find(id));
    }

    public EventTypeResponseDto createEventType(EventTypeRequestDto request) {
        return mapper.toResponse(repository.save(mapper.fromRequest(request)));
    }

    public EventTypeResponseDto updateEventType(Long id, EventTypeRequestDto request) {
        EventType eventType = find(id);

        eventType.setDescription(request.getDescription());
        List<Category> categories = request.getSuggestedCategories()
                                    .stream()
                                    .map(category -> categoryService.find(category.getId()))
                                    .toList();
        eventType.setSuggestedCategories(new ArrayList<>(categories));
        repository.save(eventType);
        return mapper.toResponse(eventType);
    }

    public void uploadImage(Long id, MultipartFile image) {
        if (image == null || image.isEmpty()) return;

        EventType eventType = find(id);
        String fileName = imageService.generateFileName(image);
        try {
            imageService.uploadImage(IMG_DIR_NAME, fileName, image);
            saveImage(eventType, fileName);
        }
        catch (IOException e) {
            throw new ImageUploadException("Failed to save profile photo.");
        }
    }

    public ImagePath getImagePath(long id) {
        EventType eventType = find(id);
        if (eventType.getImage() == null)
            throw new ImageNotFoundException("Profile photo not found.");

        return eventType.getImage();
    }

    public byte[] getImage(ImagePath path) {
        return imageService.getImage(IMG_DIR_NAME, path);
    }

    private void saveImage(EventType eventType, String fileName) throws IOException {
        String contentType = imageService.getImageContentType(IMG_DIR_NAME, fileName);
        ImagePath path = ImagePath.builder().path(fileName).contentType(contentType).build();
        eventType.setImage(path);
        repository.save(eventType);
    }

    public void deleteEventType(Long id) {
        EventType eventType = find(id);
        eventType.setDeleted(true);
        repository.save(eventType);
    }

    public List<EventType> findAllById(List<Long> eventTypesIds) {
        return repository.findAllById(eventTypesIds);
    }
}
