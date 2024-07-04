package com.jackson.utils;

import com.jackson.dto.EmployeeInfo;

public class EmployeeHolder {
    private static final ThreadLocal<EmployeeInfo> THREAD_LOCAL = new ThreadLocal<>();

    public static void setEmployeeInfo(EmployeeInfo employeeInfo) {
        THREAD_LOCAL.set(employeeInfo);
    }

    public static EmployeeInfo getEmployeeInfo() {
        return THREAD_LOCAL.get();
    }

    public static void removeEmployeeInfo() {
        THREAD_LOCAL.remove();
    }
}
