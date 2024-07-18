package com.db.dementia.dto;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@ToString
public class EmergencyContact {
    private String id;
    private String userId;
    private String name;
    private List<Long> numbers;
}
