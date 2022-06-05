package it.unibz.emails.client;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public abstract class AuthenticatedServlet extends BaseServlet {
    protected AuthenticatedServlet() {
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getSession().getAttribute("email") == null) {
            request.getRequestDispatcher("/logout").forward(request,response);
        } else {
            super.doPost(request, response);
        }
    }
}
