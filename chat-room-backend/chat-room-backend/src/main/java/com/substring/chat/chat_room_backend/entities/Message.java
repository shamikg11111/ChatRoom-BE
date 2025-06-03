package com.substring.chat.chat_room_backend.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private String messageId;             // unique ID
    private String sender;
    private String content;               // text or null
    private String imageUrl;              // image URL or null
    private LocalDateTime timeStamp;
    private List<String> deletedBy = new ArrayList<>();  // initialize to avoid NPE
    private boolean deletedForEveryone = false;

    // Convenience constructor for new messages:
    public Message(String sender, String content, String imageUrl) {
        this.messageId = UUID.randomUUID().toString();
        this.sender = sender;
        this.content = content;
        this.imageUrl = imageUrl;
        this.timeStamp = LocalDateTime.now();
        this.deletedBy = new ArrayList<>();
        this.deletedForEveryone = false;
    }
}
