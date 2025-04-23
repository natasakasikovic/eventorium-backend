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
@CrossOrigin
@RequestMapping("api/v1/notifications")
public class NotificationController implements NotificationApi {

    private final NotificationService service;

    @GetMapping
    public ResponseEntity<List<NotificationResponseDto>> getNotifications() {
        return ResponseEntity.ok(service.getAllNotifications());
    }

    @PatchMapping("/seen")
    public ResponseEntity<Void> markNotificationsAsSeen() {
        service.markAsSeen();
        return ResponseEntity.noContent().build();
    }
}