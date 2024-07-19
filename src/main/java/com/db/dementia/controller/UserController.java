package com.db.dementia.controller;

import com.db.dementia.dto.EmergencyContact;
import com.db.dementia.dto.UploadResponse;
import com.db.dementia.dto.User;
import com.db.dementia.service.DatabaseContextPath;
import com.db.dementia.service.FirebaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private final FirebaseService firebaseService;

    @PostMapping("/sign-up")
    public ResponseEntity<User> saveData(@RequestBody User user) {
        firebaseService.saveData(String.format(DatabaseContextPath.USER_NODE, user.getUid()), user);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/emergency-contact/{userId}")
    public ResponseEntity<Set<EmergencyContact>> saveEmergencyContact(@PathVariable("userId") final String userId,
                                                                      @RequestBody Set<EmergencyContact> emergencyContact)
            throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(firebaseService.saveEmergencyContact(
                String.format(DatabaseContextPath.EMERGENCY_CONTACT_USER_NODE, userId), emergencyContact));
    }

    @PutMapping("/emergency-contact/{userId}/{contactId}")
    public void updateEmergencyContact(@PathVariable("userId") final String userId,
                                       @PathVariable("contactId") final String contactId,
                                       @RequestBody EmergencyContact emergencyContact) {
        firebaseService.saveData(
                String.format(DatabaseContextPath.EMERGENCY_CONTACT_NODE, userId, contactId), emergencyContact);
    }

    @GetMapping("/emergency-contact/{userId}")
    public ResponseEntity<Set<EmergencyContact>> getEmergencyContact(@PathVariable("userId") final String userId)
            throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(firebaseService.getEmergencyContacts(
                String.format(DatabaseContextPath.EMERGENCY_CONTACT_USER_NODE, userId)));
    }

    @DeleteMapping("/emergency-contact/{userId}/{contactId}")
    public void deleteEmergencyContact(@PathVariable("userId") final String userId,
                                       @PathVariable("contactId") final String contactId) {
        firebaseService.deleteData(String.format(DatabaseContextPath.EMERGENCY_CONTACT_NODE, userId, contactId));
    }

    @GetMapping("/gallery/{userId}")
    public ResponseEntity<UploadResponse> getUploadedGallery(@PathVariable("userId") final String userId)
            throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(firebaseService.getUploadResponse(
                String.format(DatabaseContextPath.GALLERY_USER_NODE, userId), userId));
    }
}
