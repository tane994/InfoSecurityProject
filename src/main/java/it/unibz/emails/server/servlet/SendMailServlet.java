package it.unibz.emails.server.servlet;

import it.unibz.emails.client.ClientRequest;
import it.unibz.emails.client.encryption.HashAlgorithm;
import it.unibz.emails.server.persistence.entities.Mail;
import it.unibz.emails.server.persistence.entities.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;

@WebServlet("/send")
public class SendMailServlet extends AuthenticatedServlet {

    public SendMailServlet() {
        super("receiver", "subject", "body", "csrf");
    }

    @Override
    protected void handle(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String cookieContent = Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals("X-CRSF"))
                .map(cookie -> cookie.getValue())
                .findFirst()
                .orElse("");
        if (!cookieContent.equals(parameters.get("csrf")))
            throw new UserException("CSRF check failed", "/home.jsp");

        User receiver = User.get(parameters.get("receiver"));
        if (receiver == null) throw new UserException("Receiver not found", "/home.jsp");

        String digitalSignature = null;
        if (parameters.get("sign") != null) {
            digitalSignature = ClientRequest.to("/privKey")
                    .with("operation", "encrypt")
                    .with("text", new HashAlgorithm("SHA-256").hash(parameters.get("body")))
                    .with("email", getEmail(request))
                    .send()
                    .get("result");
        }
        sendMail(request,response,digitalSignature);
    }

    private void sendMail(HttpServletRequest request, HttpServletResponse response, String digitalSignature) throws ServletException, IOException {
        User sender = User.get(getEmail(request));
        User receiver = User.get(parameters.get("receiver"));
        Mail.send(sender, receiver, parameters.get("subject"), parameters.get("body"), digitalSignature);

        request.getRequestDispatcher("home.jsp").forward(request, response);
    }
}
