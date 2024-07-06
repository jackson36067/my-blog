package com.jackson.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeVO implements Serializable {
    private Long id;
    private String token;
    private String name;
    private String avatar;
    private String authority;
}
