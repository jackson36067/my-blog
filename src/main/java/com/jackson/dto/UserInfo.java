package com.jackson.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {
    private Long id;
    private String email;
    private String avatar;
    private String username;
    private LocalDateTime birthday;
}
