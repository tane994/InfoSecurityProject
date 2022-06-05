package it.unibz.emails.client;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/login")
public class Login extends BaseServlet {

    @Override
    protected void handle(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        checkCsrf(request);
        ServerRequest.to("/login").with(parameters).post();

        request.getSession().setAttribute("email", parameters.get("email"));
        request.getRequestDispatcher("/home.jsp").forward(request,response);
    }
}
