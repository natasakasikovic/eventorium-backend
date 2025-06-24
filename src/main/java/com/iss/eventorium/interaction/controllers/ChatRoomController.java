package com.iss.eventorium.interaction.controllers;

import com.iss.eventorium.interaction.api.ChatRoomApi;
import com.iss.eventorium.interaction.dtos.chat.ChatRoomResponseDto;
import com.iss.eventorium.interaction.services.ChatRoomService;
import com.iss.eventorium.shared.models.PagedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/chat-rooms")
@RequiredArgsConstructor
public class ChatRoomController implements ChatRoomApi {

    private final ChatRoomService service;

    @GetMapping("/all")
    public ResponseEntity<List<ChatRoomResponseDto>> getChatRooms() {
        return ResponseEntity.ok(service.getChatRooms());
    }

    @GetMapping
    public ResponseEntity<PagedResponse<ChatRoomResponseDto>> getChatRooms(Pageable pageable) {
        return ResponseEntity.ok(service.getChatRoomsPaged(pageable));
    }
}
