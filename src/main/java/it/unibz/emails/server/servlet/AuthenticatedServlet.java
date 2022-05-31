package it.unibz.emails.server.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public abstract class AuthenticatedServlet extends BaseServlet {
    protected AuthenticatedServlet() {
    }

    protected AuthenticatedServlet(String... parameters) {
        super(parameters);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (getEmail(request) == null) {
            response.setStatus(301);
            response.setHeader("Location", "login.jsp");
        } else {
            super.doPost(request, response);
        }
    }
}
