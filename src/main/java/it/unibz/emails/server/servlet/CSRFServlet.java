package it.unibz.emails.server.servlet;

import it.unibz.emails.server.persistence.Password;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class CSRFServlet extends BaseServlet {
    protected String csrfToken = Password.randomToken();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        super.doPost(request,response);
        Cookie csrf = new Cookie("X-CRSF", csrfToken);
        csrf.setHttpOnly(true);

        response.addCookie(csrf);
    }
}
