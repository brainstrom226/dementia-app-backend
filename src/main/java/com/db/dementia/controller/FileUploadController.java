package com.db.dementia.controller;

import com.db.dementia.dto.UploadResponse;
import com.db.dementia.service.FirebaseStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@CrossOrigin(origins = "*")
public class FileUploadController {

    @Autowired
    private FirebaseStorageService firebaseStorageService;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping(value = "/upload/{userId}", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UploadResponse> uploadFile(@PathVariable("userId") String userId,
                                                     @RequestPart("file") MultipartFile file) throws Exception {
            return ResponseEntity.ok(firebaseStorageService.uploadFile(file, userId));
    }


    @GetMapping("/download/{userId}/{fileName}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable("userId") String userId,
                                               @PathVariable("fileName") String fileName) {
        byte[] data = firebaseStorageService.downloadFile(fileName);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(data);
    }



    @DeleteMapping("/deleteAll")
    public ResponseEntity<String> deleteAllFiles() {
        try {
            firebaseStorageService.deleteAllFiles();
            return ResponseEntity.ok("All files deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to delete files: " + e.getMessage());
        }
    }
}
