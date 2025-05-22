// src/main/java/com/substring/chat/chat_room_backend/controllers/FileRetrievalController.java
package com.substring.chat.chat_room_backend.controllers;

import com.mongodb.client.gridfs.model.GridFSFile;          // <— gridfs file model
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
public class FileRetrievalController {

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @GetMapping("/api/v1/images/{fileId}")
    public ResponseEntity<GridFsResource> getImage(@PathVariable String fileId) throws IOException {
        // 1) lookup in GridFS
        GridFSFile fsFile = gridFsTemplate.findOne(
                Query.query(Criteria.where("_id").is(new ObjectId(fileId)))
        );
        if (fsFile == null) {
            return ResponseEntity.notFound().build();
        }

        // 2) fetch the resource
        GridFsResource resource = gridFsTemplate.getResource(fsFile);

        // 3) determine content-type
        String contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        if (fsFile.getMetadata() != null) {
            String ct = fsFile.getMetadata().getString("_contentType");
            if (ct != null) contentType = ct;
        }

        // 4) return the bytes
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }
}
