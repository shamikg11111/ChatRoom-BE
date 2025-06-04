package com.substring.chat.chat_room_backend.controllers;

import com.substring.chat.chat_room_backend.entities.Message;
import com.substring.chat.chat_room_backend.entities.Room;
import com.substring.chat.chat_room_backend.repositories.RoomRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.mongodb.client.gridfs.model.GridFSFile;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

@RestController
@CrossOrigin("http://localhost:5173")
@RequestMapping("/api/v1/rooms/{roomId}/images")
public class ImageController {

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @PostMapping
    public ResponseEntity<?> uploadImage(
            @PathVariable String roomId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("sender") String sender,
            // ← NEW PARAMETER (optional) for “reply to”:
            @RequestParam(value = "replyToMessageId", required = false) String replyToMessageId
    ) throws IOException {
        // Store in GridFS exactly as before:
        ObjectId fileId = gridFsTemplate.store(
                file.getInputStream(),
                file.getOriginalFilename(),
                file.getContentType()
        );
        String imageUrl = "/api/v1/images/" + fileId.toString();

        // Build Message with the new constructor (pass replyToMessageId at the end):
        Message msg = new Message(
                sender,
                null,
                imageUrl,
                new ArrayList<>(),     // no mentions recomputed here
                replyToMessageId      // may be null if no “reply.”
        );

        // Save into room and broadcast exactly as before:
        Room room = roomRepository.findByRoomId(roomId);
        if (room == null) {
            return ResponseEntity.badRequest().body("Room not found: " + roomId);
        }
        room.getMessages().add(msg);
        roomRepository.save(room);
        messagingTemplate.convertAndSend("/topic/room/" + roomId, msg);

        return ResponseEntity.ok(Collections.singletonMap("imageUrl", imageUrl));
    }
}
