package com.db.dementia.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class User {
    @NonNull
    private String uid;
    @NonNull
    private String email;
    @NonNull
    private String password;
    @NonNull
    private Role role;
}
