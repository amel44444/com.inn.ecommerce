package com.inn.ecommerce.utils;


public class JwtRequestUtil {

    private static final ThreadLocal<String> currentRole = new ThreadLocal<>();

    public static void setCurrentRole(String role) {
        currentRole.set(role);
    }

    public static String getCurrentRole() {
        return currentRole.get();
    }

    public static boolean isAdmin() {
        return "admin".equalsIgnoreCase(getCurrentRole());
    }

    public static void clear() {
        currentRole.remove();
    }
}
