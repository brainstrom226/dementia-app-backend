package com.db.dementia.service;

import com.db.dementia.dto.EmergencyContact;
import com.google.firebase.database.*;
import com.google.gson.Gson;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@DependsOn("firebaseApp")
public class FirebaseService {

    private final DatabaseReference databaseReference;
    private final Gson gson = new Gson();

    public FirebaseService() {
        this.databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void saveData(final String key, final Object value) {
        databaseReference.child(key).setValueAsync(value);
    }

    public void deleteData(final String key) throws RuntimeException {
        databaseReference.child(key).removeValue((databaseError, databaseReference) -> {
            if (Objects.nonNull(databaseError)) {
                System.out.println("Delete failed with " + databaseError.getMessage());
                throw new RuntimeException(databaseError.getMessage());
            }
        });
    }

    public Set<EmergencyContact> saveEmergencyContact(final String key, final Set<EmergencyContact> values) throws ExecutionException, InterruptedException {
        final CompletableFuture<Set<EmergencyContact>> futureContacts = new CompletableFuture<>();
        final Set<EmergencyContact> emergencyContacts = new HashSet<>();
        final DatabaseReference databaseRef = databaseReference.child(key);

        databaseRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {

                values.forEach(value -> {
                    final DatabaseReference newContactRef = databaseRef.push();
                    mutableData.child(newContactRef.getKey()).setValue(value);
                });

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                if (Objects.nonNull(databaseError)) {
                    System.out.println("Transaction failed with " + databaseError.getMessage());
                } else {
                    dataSnapshot.getChildren().forEach(child -> {
                        final EmergencyContact emergencyContact =
                                gson.fromJson(gson.toJson(child.getValue()), EmergencyContact.class);
                        emergencyContact.setId(child.getKey());
                        emergencyContacts.add(emergencyContact);
                    });
                    futureContacts.complete(emergencyContacts);
                }
            }
        });

        return futureContacts.get();
    }

    public Set<EmergencyContact> getEmergencyContacts(final String key) throws ExecutionException, InterruptedException {
        final CompletableFuture<Set<EmergencyContact>> futureContacts = new CompletableFuture<>();
        final Set<EmergencyContact> emergencyContacts = new HashSet<>();
        databaseReference.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataSnapshot.getChildren().forEach(child -> {
                    final EmergencyContact emergencyContact =
                            gson.fromJson(gson.toJson(child.getValue()), EmergencyContact.class);
                    emergencyContact.setId(child.getKey());
                    emergencyContacts.add(emergencyContact);
                });
                futureContacts.complete(emergencyContacts);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Transaction failed with " + databaseError.getMessage());
            }
        });
        return futureContacts.get();
    }
}