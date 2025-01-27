package com.iss.eventorium.notifications.controllers;

import com.iss.eventorium.notifications.dtos.NotificationResponseDto;
import com.iss.eventorium.notifications.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("api/v1/notifications")
public class NotificationController {

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