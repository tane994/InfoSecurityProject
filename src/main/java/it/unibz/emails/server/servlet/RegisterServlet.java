package it.unibz.emails.server.servlet;

import it.unibz.emails.client.ClientRequest;
import it.unibz.emails.server.persistence.entities.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet("/register")
public class RegisterServlet extends BaseServlet {
    private final String emailRegex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";

    public RegisterServlet() {
        super("name", "surname", "email", "password");
    }

    @Override
    protected void handle(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!parameters.get("email").matches(emailRegex))
            throw new UserException("Invalid email inserted", "/register.jsp");
        boolean alreadyRegistered = User.get(parameters.get("email")) != null;
        if (alreadyRegistered)
            throw new UserException("Email already present", "/register.jsp");

        Map<String,String> keys = ClientRequest.to("/generateKeys").with("email", parameters.get("email")).send();

        User.set(parameters.get("name"), parameters.get("surname"), parameters.get("email"), parameters.get("password"), Integer.valueOf(keys.get("pubkey")), Integer.valueOf(keys.get("exponent")));
        System.out.println("Registration succeeded!");

        request.getSession().setAttribute("email", parameters.get("email"));
        request.getRequestDispatcher("/home.jsp").forward(request, response);
    }
}
