package it.unibz.emails.server.servlet;

import it.unibz.emails.server.persistence.Password;
import it.unibz.emails.server.persistence.entities.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends BaseServlet {

    public LoginServlet() {
        super("email", "password");
    }

    @Override
    protected void handle(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = User.get(parameters.get("email"));
        if (user==null || !Password.areSame(parameters.get("password"),user.getPassword()))
            throw new UserException("Login failed", "/login.jsp");

        System.out.println("Login succeeded");
        request.getSession().setAttribute("email", user.getEmail());
        request.getRequestDispatcher("/home.jsp").forward(request, response);
    }
}
