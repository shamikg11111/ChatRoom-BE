package com.substring.chat.chat_room_backend.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MessageRequest {
    private String content;
    private String sender;
    private String roomId;
    private String imageUrl;              // may be null if this is a text‐only message
    private List<String> mentionedUsers;  // backend recomputes this, but keep it here
    // ← NEW FIELD:
    private String replyToMessageId;      // ID of the message being replied to (or null)

    private LocalDateTime messageTime;
}
