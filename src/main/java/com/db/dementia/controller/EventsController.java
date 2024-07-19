package com.db.dementia.controller;

import com.db.dementia.dto.EventDTO;
import com.db.dementia.service.DatabaseContextPath;
import com.db.dementia.service.EventsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/events")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class EventsController {
    private final EventsService eventsService;

    @PostMapping("/events/{userId}")
    public ResponseEntity<Set<EventDTO>> saveEmergencyContact(@PathVariable("userId") final String userId,
                                                              @RequestBody Set<EventDTO> eventDTOS)
            throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(eventsService.saveEventDTO(
                String.format(DatabaseContextPath.EVENTS_USER_NODE, userId), eventDTOS, userId));
    }
    @GetMapping("/events/{userId}")
    public ResponseEntity<Set<EventDTO>> getEmergencyContact(@PathVariable("userId") final String userId)
            throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(eventsService.getEventDTOs(
                String.format(DatabaseContextPath.EVENTS_USER_NODE, userId), userId));
    }
    @DeleteMapping("/events/{userId}/{eventId}")
    public void deleteEmergencyContact(@PathVariable("userId") final String userId,
                                       @PathVariable("eventId") final String eventId) {
        eventsService.deleteData(String.format(DatabaseContextPath.EVENTS_NODE, userId, eventId));
    }
}
