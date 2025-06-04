package com.substring.chat.chat_room_backend.controllers;

import com.substring.chat.chat_room_backend.entities.Message;
import com.substring.chat.chat_room_backend.entities.Room;
import com.substring.chat.chat_room_backend.payload.MessageRequest;
import com.substring.chat.chat_room_backend.repositories.RoomRepository;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class ChatController {

    private final RoomRepository roomRepository;

    public ChatController(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    // Regex to find @username tokens (alphanumeric + underscore)
    private static final Pattern AT_PATTERN = Pattern.compile("@(\\w+)");

    @MessageMapping("/sendMessage/{roomId}")
    @SendTo("/topic/room/{roomId}")
    public Message sendMessage(
            @DestinationVariable String roomId,
            MessageRequest request) {

        Room room = roomRepository.findByRoomId(request.getRoomId());
        if (room == null) {
            throw new RuntimeException("Room not found!");
        }

        // Extract mentionedUsers exactly as before:
        List<String> mentionedUsers = new ArrayList<>();
        if (request.getContent() != null) {
            Matcher matcher = AT_PATTERN.matcher(request.getContent());
            Set<String> found = new HashSet<>();
            while (matcher.find()) {
                found.add(matcher.group(1));
            }
            if (!found.isEmpty()) {
                // Gather all existing senders + the new sender
                Set<String> currentSenders = new HashSet<>();
                for (Message m : room.getMessages()) {
                    currentSenders.add(m.getSender());
                }
                currentSenders.add(request.getSender());

                for (String u : found) {
                    if (!u.equals(request.getSender()) && currentSenders.contains(u)) {
                        mentionedUsers.add(u);
                    }
                }
            }
        }

        // ← HERE IS THE ONLY CHANGE: call the new constructor, adding request.getReplyToMessageId()
        Message message = new Message(
                request.getSender(),
                request.getContent(),
                request.getImageUrl(),
                mentionedUsers,
                request.getReplyToMessageId()
        );

        // Save & broadcast exactly as before:
        room.getMessages().add(message);
        roomRepository.save(room);
        return message;
    }
}
