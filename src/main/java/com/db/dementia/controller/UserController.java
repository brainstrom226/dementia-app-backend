package com.db.dementia.controller;

import com.db.dementia.dto.User;
import com.db.dementia.service.DatabaseContextPath;
import com.db.dementia.service.FirebaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private final FirebaseService firebaseService;

    @PostMapping("/sign-up")
    public void saveData(@RequestBody User user) {
        firebaseService.saveData(DatabaseContextPath.USER_NODE+"/"+user.getUid(), user);
    }
}
