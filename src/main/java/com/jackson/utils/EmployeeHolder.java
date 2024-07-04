package com.jackson.utils;

public class EmployeeHolder {
    private static final ThreadLocal<Long> THREAD_LOCAL = new ThreadLocal<>();

    public static void setEmployeeId(Long empId) {
        THREAD_LOCAL.set(empId);
    }

    public static long getEmployeeId() {
        return THREAD_LOCAL.get();
    }

    public static void removeEmployeeId() {
        THREAD_LOCAL.remove();
    }
}
