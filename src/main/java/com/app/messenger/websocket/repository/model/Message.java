package com.app.messenger.websocket.repository.model;

import com.app.messenger.repository.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "sender_id", referencedColumnName = "id")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "chat_id", referencedColumnName = "id", nullable = false)
    private Chat chat;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private ZonedDateTime sendTime;

    @Enumerated(EnumType.STRING)
    private MessageType type;

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL)
    private List<MessageStatus> statuses;
}
