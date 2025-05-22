// src/main/java/com/substring/chat/chat_room_backend/controllers/ImageController.java
package com.substring.chat.chat_room_backend.controllers;

import com.substring.chat.chat_room_backend.entities.Message;
import com.substring.chat.chat_room_backend.entities.Room;
import com.substring.chat.chat_room_backend.repositories.RoomRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;      // <— GridFS template
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;             // <— MultipartFile import
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

@RestController
@CrossOrigin("http://localhost:5173")
@RequestMapping("/api/v1/rooms/{roomId}/images")
public class ImageController {

    @Autowired private GridFsTemplate gridFsTemplate;
    @Autowired private RoomRepository roomRepository;
    @Autowired private SimpMessagingTemplate messagingTemplate;

    @PostMapping
    public ResponseEntity<Map<String, String>> uploadImage(
            @PathVariable String roomId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("sender") String sender
    ) throws IOException {
        // store file bytes
        ObjectId fileId = gridFsTemplate.store(
                file.getInputStream(),
                file.getOriginalFilename(),
                file.getContentType()
        );

        // build retrieval URL
        String imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/images/")
                .path(fileId.toHexString())
                .toUriString();

        // create & persist Message
        Message msg = new Message(sender, null, imageUrl);
        msg.setTimeStamp(LocalDateTime.now());
        Room room = roomRepository.findByRoomId(roomId);
        room.getMessages().add(msg);
        roomRepository.save(room);

        // broadcast via WebSocket
        messagingTemplate.convertAndSend("/topic/room/" + roomId, msg);

        // return the URL for the client
        return ResponseEntity.ok(Collections.singletonMap("imageUrl", imageUrl));
    }
}
