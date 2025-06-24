package com.iss.eventorium.interaction.models;


import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "chat_rooms")
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    private ChatMessage lastMessage;

    public ChatRoom(String name, ChatMessage lastMessage) {
        this.name = name;
        this.lastMessage = lastMessage;
    }
}
