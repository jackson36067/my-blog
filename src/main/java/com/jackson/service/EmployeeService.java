package com.jackson.service;

import com.jackson.dto.EmployeeDTO;
import com.jackson.dto.EmployeeLoginDTO;
import com.jackson.entity.Result;

public interface EmployeeService {
    Result addEmployee(EmployeeDTO employeeDTO);

    Result login(EmployeeLoginDTO employeeLoginDTO);
}
