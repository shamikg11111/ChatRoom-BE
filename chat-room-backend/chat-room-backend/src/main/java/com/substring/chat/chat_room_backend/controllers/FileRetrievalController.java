// src/main/java/com/substring/chat/chat_room_backend/controllers/FileRetrievalController.java
package com.substring.chat.chat_room_backend.controllers;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.data.mongodb.gridfs.GridFsResource;

import com.mongodb.client.gridfs.model.GridFSFile;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
public class FileRetrievalController {

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @GetMapping("/api/v1/images/{fileId}")
    public ResponseEntity<GridFsResource> getImage(@PathVariable String fileId) throws IOException {
        GridFSFile fsFile = gridFsTemplate.findOne(
                new Query(Criteria.where("_id").is(new ObjectId(fileId)))
        );
        if (fsFile == null) return ResponseEntity.notFound().build();

        GridFsResource resource = gridFsTemplate.getResource(fsFile);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fsFile.getMetadata().getString("_contentType")))
                .body(resource);
    }
}
