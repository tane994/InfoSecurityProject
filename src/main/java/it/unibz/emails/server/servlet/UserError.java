package it.unibz.emails.server.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class UserError {
    public static void handle(HttpServletRequest req, HttpServletResponse resp, String message, String redirectPath) throws ServletException, IOException {
        req.setAttribute("error", message);
        req.getRequestDispatcher(redirectPath).forward(req, resp);
    }
}
