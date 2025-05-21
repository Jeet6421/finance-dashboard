package com.financedash.finance_dashboard.utils;

public class EmailUtils {

    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "invalid_email";
        }
        String[] parts = email.split("@");
        String username = parts[0];
        String domain = parts[1];
        if (username.length() <= 1) return "*@" + domain;
        return username.charAt(0) + "***@" + domain;
    }

    public static String obfuscateToken(String token) {
        if (token == null || token.length() < 10) return "********";
        return token.substring(0, 5) + "..." + token.substring(token.length() - 5);
    }
}