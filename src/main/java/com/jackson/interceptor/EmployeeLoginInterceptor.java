package com.jackson.interceptor;

import com.jackson.constant.JwtConstant;
import com.jackson.service.impl.EmployeeServiceImpl;
import com.jackson.utils.EmployeeHolder;
import com.jackson.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
@Slf4j
public class EmployeeLoginInterceptor implements HandlerInterceptor {

    @Resource
    private EmployeeServiceImpl employeeService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //判断当前拦截到的是Controller的方法还是其他资源
        if (!(handler instanceof HandlerMethod)) {
            //当前拦截到的不是动态方法，直接放行
            return true;
        }
        // 从请求头中获取token
        String token = request.getHeader("token");
        if (token == null) {
            log.info("token为空");
            response.setStatus(401);
            return false;
        }
        try {
            Claims claims = JwtUtils.parseJwt(token);
            Long empId = Long.valueOf(claims.get(JwtConstant.EMP_Id).toString());
            EmployeeHolder.setEmployeeId(empId);
            return true;
        } catch (Exception e) {
            log.info("token解析失败");
            response.setStatus(401);
            return false;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }
}
