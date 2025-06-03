// src/main/java/com/substring/chat/chat_room_backend/controllers/RoomController.java
package com.substring.chat.chat_room_backend.controllers;

import com.substring.chat.chat_room_backend.entities.Message;
import com.substring.chat.chat_room_backend.entities.Room;
import com.substring.chat.chat_room_backend.repositories.RoomRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/rooms")
@CrossOrigin("http://localhost:5173")
public class RoomController {

    private final RoomRepository roomRepository;

    public RoomController(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    // create room
    @PostMapping
    public ResponseEntity<?> createRoom(@RequestBody String roomId) {
        if (roomRepository.findByRoomId(roomId) != null) {
            return ResponseEntity.badRequest().body("Room already exists");
        }
        Room room = new Room();
        room.setRoomId(roomId);
        roomRepository.save(room);
        return ResponseEntity.status(HttpStatus.CREATED).body(room);
    }

    // join/get room
    @GetMapping("/{roomId}")
    public ResponseEntity<?> joinRoom(@PathVariable String roomId) {
        Room room = roomRepository.findByRoomId(roomId);
        if (room == null) {
            return ResponseEntity.badRequest().body("Room not found!");
        }
        return ResponseEntity.ok(room);
    }

    // get messages of the room (with backfill of missing messageId)
    @GetMapping("/{roomId}/messages")
    public ResponseEntity<List<Message>> getMessages(
            @PathVariable String roomId,
            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @RequestParam(value = "size", defaultValue = "20", required = false) int size
    ) {
        Room room = roomRepository.findByRoomId(roomId);
        if (room == null) {
            return ResponseEntity.badRequest().build();
        }

        // Backfill any Message whose messageId is null
        boolean changed = false;
        for (Message msg : room.getMessages()) {
            if (msg.getMessageId() == null || msg.getMessageId().isEmpty()) {
                msg.setMessageId(UUID.randomUUID().toString());
                changed = true;
            }
        }
        if (changed) {
            // Persist the newly assigned IDs
            roomRepository.save(room);
        }

        List<Message> messages = room.getMessages();
        int start = Math.max(0, messages.size() - (page + 1) * size);
        int end = Math.min(messages.size(), start + size);
        List<Message> paginatedMessages = messages.subList(start, end);
        return ResponseEntity.ok(paginatedMessages);
    }
}
