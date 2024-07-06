package com.jackson.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jackson.constant.BaseConstant;
import com.jackson.constant.JwtConstant;
import com.jackson.constant.PasswordConstant;
import com.jackson.dto.EmployeeDTO;
import com.jackson.dto.EmployeeLoginDTO;
import com.jackson.entity.Employee;
import com.jackson.entity.Result;
import com.jackson.exception.*;
import com.jackson.mapper.EmployeeMapper;
import com.jackson.service.EmployeeService;
import com.jackson.utils.JwtUtils;
import com.jackson.vo.EmployeeVO;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
    /**
     * 新增员工
     *
     * @param employeeDTO
     * @return
     */
    @Override
    public Result addEmployee(EmployeeDTO employeeDTO) {
        Employee employee = this.query().eq("username", employeeDTO.getUsername()).one();
        // 根据用户名判断用户是否存在
        if (employee != null) {
            throw new UserExistException(BaseConstant.USER_EXIST);
        }
        employee = BeanUtil.copyProperties(employeeDTO, Employee.class);
        // 把密码进行加密
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));
        this.save(employee);
        return Result.success();
    }

    @Override
    public Result login(EmployeeLoginDTO employeeLoginDTO) {
        Employee employee = this.query().eq("username", employeeLoginDTO.getUsername()).one();
        // 判断用户是否存在
        if (employee == null) {
            throw new UserNotFoundException(BaseConstant.USER_NOT_FOUND);
        }
        // 判断用户名密码是否正确
        if (!Objects.equals(employee.getPassword(), DigestUtils.md5DigestAsHex(employeeLoginDTO.getPassword().getBytes()))) {
            throw new PasswordErrorException(BaseConstant.PASSWORD_ERROR);
        }
        // 校验用户账号的状态
        if (employee.getStatus().equals("0")) {
            throw new AccountLockedException(BaseConstant.ACCOUNT_LOCKED);
        }
        // 生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtConstant.EMP_Id, employee.getId());
        String token = JwtUtils.genJwt(claims);
        // 封装返回参数employeeVO
        EmployeeVO employeeVO = BeanUtil.copyProperties(employee, EmployeeVO.class);
        employeeVO.setToken(token);
        return Result.success(token);
    }
}
