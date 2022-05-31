package it.unibz.emails.server;

import jakarta.servlet.http.HttpServletResponse;

public class Headers {
    public static void set(HttpServletResponse resp) {
        resp.setHeader("X-Content-Type-Options", "nosniff");
        resp.setHeader("X-Frame-Options", "DENY");
        resp.setHeader("X-XSS-Protection", "1; mode=block");
        resp.setHeader("Content-Security-Policy", "default-src 'none'; img-src 'self'; script-src 'self'; style-src 'self'");
        resp.setHeader("Referrer-Policy", "same-origin");
    }
}
