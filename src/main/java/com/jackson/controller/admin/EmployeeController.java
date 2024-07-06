package com.jackson.controller.admin;

import com.jackson.dto.EmployeeDTO;
import com.jackson.dto.EmployeeLoginDTO;
import com.jackson.entity.Result;
import com.jackson.service.EmployeeService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/employee")
public class EmployeeController {

    @Resource
    private EmployeeService employeeService;

    /**
     * 新增员工
     *
     * @return
     */
    @PostMapping("/add")
    public Result addEmployee(@RequestBody EmployeeDTO employeeDTO) {
        return employeeService.addEmployee(employeeDTO);
    }

    /**
     * 登录功能
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    public Result login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        return employeeService.login(employeeLoginDTO);
    }
}
