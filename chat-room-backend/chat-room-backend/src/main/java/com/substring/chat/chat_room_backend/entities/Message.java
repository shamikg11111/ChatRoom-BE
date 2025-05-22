package com.substring.chat.chat_room_backend.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private String sender;
    private String content;
    private String imageUrl;
    private LocalDateTime timeStamp;

    /**
     * Convenience constructor for text-or-image messages.
     * @param sender    the username who sent it
     * @param content   the text content (null for pure-image)
     * @param imageUrl  the image URL    (null for pure-text)
     */
    public Message(String sender, String content, String imageUrl) {
        this.sender = sender;
        this.content = content;
        this.imageUrl = imageUrl;
        this.timeStamp = LocalDateTime.now();
    }
}
