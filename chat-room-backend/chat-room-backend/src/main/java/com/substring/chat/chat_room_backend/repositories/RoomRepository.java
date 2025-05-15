package com.substring.chat.chat_room_backend.repositories;

import com.substring.chat.chat_room_backend.entities.Room;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoomRepository extends MongoRepository<Room, String> {
// get room by room id
    Room findByRoomId(String roomId);

}
