package it.unibz.emails.client;

import it.unibz.emails.client.encryption.RSA;
import it.unibz.emails.server.persistence.entities.User;
import it.unibz.emails.server.servlet.BaseServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/privKey")
public class PrivKeyServlet extends BaseServlet {

    public PrivKeyServlet() {
        super("email", "operation", "text");
    }

    @Override
    protected void handle(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String operation = parameters.get("operation");
        String text = parameters.get("text");

        User user = User.get(parameters.get("email"));
        Integer privKey = (Integer) db.select("SELECT privkey FROM privkeys WHERE email=?", user.getEmail()).get(0).get("privkey");

        String result;
        if (operation.equals("encrypt")) result = RSA.encrypt(text, privKey, user.getExponent());
        else result = RSA.decrypt(text, privKey, user.getExponent());

        response.setContentType("text/plain");
        response.getWriter().println("result=" + result);
    }
}
