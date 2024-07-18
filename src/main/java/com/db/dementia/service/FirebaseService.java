package com.db.dementia.service;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

@Service
@DependsOn("firebaseApp")
public class FirebaseService {

    private final DatabaseReference databaseReference;

    public FirebaseService() {
        this.databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void saveData(String key, Object value) {
        databaseReference.child(key).setValueAsync(value);
    }

    public DatabaseReference getData(String key) {
        return databaseReference.child(key);
    }
}