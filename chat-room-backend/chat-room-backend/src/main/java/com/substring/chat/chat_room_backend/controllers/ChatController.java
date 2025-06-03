// src/main/java/com/substring/chat/chat_room_backend/controllers/ChatController.java
package com.substring.chat.chat_room_backend.controllers;

import com.substring.chat.chat_room_backend.entities.Message;
import com.substring.chat.chat_room_backend.entities.Room;
import com.substring.chat.chat_room_backend.payload.MessageRequest;
import com.substring.chat.chat_room_backend.repositories.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

@Controller
@CrossOrigin("http://localhost:5173")
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final RoomRepository roomRepository;

    @Autowired
    public ChatController(SimpMessagingTemplate messagingTemplate,
                          RoomRepository roomRepository) {
        this.messagingTemplate = messagingTemplate;
        this.roomRepository = roomRepository;
    }

    /**
     * Listens for new messages (text or image) coming from the frontend.
     * Builds a Message (with assigned messageId), persists it, and broadcasts.
     */
    @MessageMapping("/sendMessage/{roomId}")
    public void sendMessage(
            @DestinationVariable String roomId,
            MessageRequest request
    ) {
        // 1) Construct a new Message with its own messageId
        Message msg = new Message(
                request.getSender(),
                request.getContent(),
                request.getImageUrl()
        );

        // 2) Persist into MongoDB
        Room room = roomRepository.findByRoomId(roomId);
        if (room == null) throw new RuntimeException("Room not found: " + roomId);

        room.getMessages().add(msg);
        roomRepository.save(room);

        // 3) Broadcast the newly-created Message to all subscribers
        messagingTemplate.convertAndSend("/topic/room/" + roomId, msg);
    }
}
