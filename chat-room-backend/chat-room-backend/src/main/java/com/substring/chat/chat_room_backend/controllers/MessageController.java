package com.substring.chat.chat_room_backend.controllers;

import com.substring.chat.chat_room_backend.entities.Message;
import com.substring.chat.chat_room_backend.entities.Room;
import com.substring.chat.chat_room_backend.repositories.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("http://localhost:5173")
@RequestMapping("/api/v1/rooms/{roomId}/messages")
public class MessageController {

    private final RoomRepository roomRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public MessageController(RoomRepository roomRepository,
                             SimpMessagingTemplate messagingTemplate) {
        this.roomRepository = roomRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @DeleteMapping("/{messageId}/deleteForMe")
    public ResponseEntity<?> deleteForMe(
            @PathVariable String roomId,
            @PathVariable String messageId,
            @RequestParam(value = "user", required = false) String user
    ) {
        System.out.println("DELETE /api/v1/rooms/" + roomId +
                "/messages/" + messageId + "/deleteForMe?user=" + user);

        Room room = roomRepository.findByRoomId(roomId);
        if (room == null) {
            System.out.println("  → Room not found: " + roomId);
            return ResponseEntity.badRequest().body("Room not found: " + roomId);
        }

        List<Message> msgs = room.getMessages();
        Message target = null;
        for (Message msg : msgs) {
            // Safe check: messageId on DB item might be null, so reverse equals
            if (messageId.equals(msg.getMessageId())) {
                target = msg;
                break;
            }
        }
        if (target == null) {
            System.out.println("  → Message not found: " + messageId);
            return ResponseEntity.badRequest().body("Message not found: " + messageId);
        }

        if (user != null && !target.getDeletedBy().contains(user)) {
            target.getDeletedBy().add(user);
        }
        roomRepository.save(room);

        messagingTemplate.convertAndSend("/topic/room/" + roomId, target);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{messageId}/deleteForEveryone")
    public ResponseEntity<?> deleteForEveryone(
            @PathVariable String roomId,
            @PathVariable String messageId
    ) {
        System.out.println("DELETE /api/v1/rooms/" + roomId +
                "/messages/" + messageId + "/deleteForEveryone");

        Room room = roomRepository.findByRoomId(roomId);
        if (room == null) {
            System.out.println("  → Room not found: " + roomId);
            return ResponseEntity.badRequest().body("Room not found: " + roomId);
        }

        Message target = null;
        for (Message msg : room.getMessages()) {
            if (messageId.equals(msg.getMessageId())) {
                target = msg;
                break;
            }
        }
        if (target == null) {
            System.out.println("  → Message not found: " + messageId);
            return ResponseEntity.badRequest().body("Message not found: " + messageId);
        }

        target.setDeletedForEveryone(true);
        target.setContent(null);
        target.setImageUrl(null);
        roomRepository.save(room);

        messagingTemplate.convertAndSend("/topic/room/" + roomId, target);
        return ResponseEntity.ok().build();
    }
}
