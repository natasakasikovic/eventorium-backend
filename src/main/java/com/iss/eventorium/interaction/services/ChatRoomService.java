package com.iss.eventorium.interaction.services;

import com.iss.eventorium.interaction.dtos.chat.ChatRoomResponseDto;
import com.iss.eventorium.interaction.mappers.ChatMapper;
import com.iss.eventorium.interaction.models.ChatMessage;
import com.iss.eventorium.interaction.models.ChatRoom;
import com.iss.eventorium.interaction.repositories.ChatRoomRepository;
import com.iss.eventorium.shared.models.PagedResponse;
import com.iss.eventorium.user.models.User;
import com.iss.eventorium.user.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.iss.eventorium.interaction.mappers.ChatMapper.toPagedResponse;
import static com.iss.eventorium.interaction.mappers.ChatMapper.toResponse;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository repository;
    private final AuthService authService;

    public void createChatRoom(User sender, User recipient, ChatMessage message) {
        String senderRoomName = generateChatRoomName(sender, recipient);
        String recipientRoomName = generateChatRoomName(recipient, sender);

        Optional<ChatRoom> senderRoom = repository.findByName(senderRoomName);
        if(senderRoom.isPresent()) {
            updateChatRoom(senderRoom.get(), message);
            updateChatRoom(repository.findByName(recipientRoomName).get(), message);
            return;
        }

        repository.save(new ChatRoom(senderRoomName, message));
        repository.save(new ChatRoom(recipientRoomName, message));
    }

    public List<ChatRoomResponseDto> getChatRooms() {
        User currentUser = authService.getCurrentUser();
        return repository.findChatRooms(currentUser.getId() + "_%").stream()
                .map(room -> toResponse(room, currentUser))
                .toList();
    }

    public PagedResponse<ChatRoomResponseDto> getChatRoomsPaged(Pageable pageable) {
        User currentUser = authService.getCurrentUser();
        return toPagedResponse(repository.findChatRooms(currentUser.getId() + "_%", pageable), currentUser);
    }

    private void updateChatRoom(ChatRoom room, ChatMessage message) {
        room.setLastMessage(message);
        repository.save(room);
    }

    private String generateChatRoomName(User sender, User recipient) {
        return sender.getId() + "_" + recipient.getId();
    }
}
