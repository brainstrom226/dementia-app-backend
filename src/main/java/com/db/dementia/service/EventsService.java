package com.db.dementia.service;

import com.db.dementia.dto.EventDTO;
import com.google.firebase.database.*;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@DependsOn("firebaseApp")
@RequiredArgsConstructor
public class EventsService {
    
    private final DatabaseReference databaseReference;
    private final Gson gson = new Gson();

    public EventsService() {
        this.databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void deleteData(final String key) throws RuntimeException {
        databaseReference.child(key).removeValue((databaseError, databaseReference) -> {
            if (Objects.nonNull(databaseError)) {
                System.out.println("Delete failed with " + databaseError.getMessage());
                throw new RuntimeException(databaseError.getMessage());
            }
        });
    }

    public Set<EventDTO> saveEventDTO(final String key, final Set<EventDTO> values, final String user)
            throws ExecutionException, InterruptedException {
        final CompletableFuture<Set<EventDTO>> futureEvents = new CompletableFuture<>();
        final Set<EventDTO> eventDTOS = new HashSet<>();
        final DatabaseReference databaseRef = databaseReference.child(key);

        databaseRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {

                values.forEach(value -> {
                    value.setEventId(null);
                    value.setUserId(null);
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
                        final EventDTO eventDTO =
                                gson.fromJson(gson.toJson(child.getValue()), EventDTO.class);
                        eventDTO.setEventId(child.getKey());
                        eventDTO.setUserId(user);
                        eventDTOS.add(eventDTO);
                    });
                    futureEvents.complete(eventDTOS);
                }
            }
        });

        return futureEvents.get();
    }

    public Set<EventDTO> getEventDTOs(final String key, final String user) throws ExecutionException, InterruptedException {
        final CompletableFuture<Set<EventDTO>> futureContacts = new CompletableFuture<>();
        final Set<EventDTO> eventDTOS = new HashSet<>();
        databaseReference.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataSnapshot.getChildren().forEach(child -> {
                    final EventDTO eventDTO =
                            gson.fromJson(gson.toJson(child.getValue()), EventDTO.class);
                    eventDTO.setEventId(child.getKey());
                    eventDTO.setUserId(user);
                    eventDTOS.add(eventDTO);
                });
                futureContacts.complete(eventDTOS);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Transaction failed with " + databaseError.getMessage());
            }
        });
        return futureContacts.get();
    }
}
