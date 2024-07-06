package com.jackson.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDTO implements Serializable {
    private String name;
    private String username;
    private String phone;
    private String avatar;
    private String idNumber;
    private String authority;
    private String sex;
}
