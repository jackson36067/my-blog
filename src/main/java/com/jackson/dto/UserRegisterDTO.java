package com.jackson.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterDTO {
    private String email;
    private String userName;
    private String password;
    private LocalDate birthday;
    private String code;
}
