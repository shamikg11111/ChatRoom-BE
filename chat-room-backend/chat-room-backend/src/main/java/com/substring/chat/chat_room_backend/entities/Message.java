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
    private String messageId;                // unique ID
    private String sender;
    private String content;                  // text‐only, or null if image‐only
    private String imageUrl;                 // image URL (or null if text‐only)
    private LocalDateTime timeStamp;
    private List<String> deletedBy = new ArrayList<>();  // soft‐delete list
    private boolean deletedForEveryone = false;
    private List<String> mentionedUsers = new ArrayList<>();

    // ← NEW FIELD:
    private String replyToMessageId;         // the ID of the message being replied to (or null)

    /**
     * Convenience constructor for new Messages (text‐only or image‐only, plus mentions & replyTo).
     * Add replyToMessageId as the last argument.
     */
    public Message(
            String sender,
            String content,
            String imageUrl,
            List<String> mentionedUsers,
            String replyToMessageId
    ) {
        this.messageId = UUID.randomUUID().toString();
        this.sender = sender;
        this.content = content;
        this.imageUrl = imageUrl;
        this.timeStamp = LocalDateTime.now();
        this.deletedBy = new ArrayList<>();
        this.deletedForEveryone = false;
        this.mentionedUsers = (mentionedUsers != null ? mentionedUsers : new ArrayList<>());
        this.replyToMessageId = replyToMessageId;
    }
}
