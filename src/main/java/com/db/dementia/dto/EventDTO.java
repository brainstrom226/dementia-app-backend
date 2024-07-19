package com.db.dementia.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventDTO {
    private String userId;
    private String eventId;
    private String name;
    private String description;
    private Long timeInMillis;
}
