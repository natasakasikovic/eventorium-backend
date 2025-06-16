package com.iss.eventorium.notifications.controllers;

import com.iss.eventorium.notifications.api.NotificationApi;
import com.iss.eventorium.notifications.dtos.NotificationResponseDto;
import com.iss.eventorium.notifications.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/notifications")
public class NotificationController implements NotificationApi {

    private final NotificationService service;

    @GetMapping
    public ResponseEntity<List<NotificationResponseDto>> getNotifications() {
        return ResponseEntity.ok(service.getAllNotifications());
    }

    @GetMapping("/silence")
    public ResponseEntity<Boolean> getSilenceStatus() {
        return ResponseEntity.ok(service.getSilenceStatus());
    }

    @PatchMapping("/silence")
    public ResponseEntity<Void> silenceNotifications(@RequestParam boolean silence) {
        service.silenceNotifications(silence);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/seen")
    public ResponseEntity<Void> markNotificationsAsSeen() {
        service.markAsSeen();
        return ResponseEntity.noContent().build();
    }
}