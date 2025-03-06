package com.iss.eventorium.interaction.services;

import com.iss.eventorium.interaction.models.ChatMessage;
import com.iss.eventorium.interaction.models.ChatRoom;
import com.iss.eventorium.interaction.repositories.ChatRoomRepository;
import com.iss.eventorium.user.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository repository;

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

    private void updateChatRoom(ChatRoom room, ChatMessage message) {
        room.setLastMessage(message);
        repository.save(room);
    }

    private String generateChatRoomName(User sender, User recipient) {
        return sender.getId() + "_" + recipient.getId();
    }
}
