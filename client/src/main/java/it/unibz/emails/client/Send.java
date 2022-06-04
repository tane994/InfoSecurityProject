package it.unibz.emails.client;

import it.unibz.emails.client.encryption.HashAlgorithm;
import it.unibz.emails.client.encryption.RSA;
import it.unibz.emails.entities.Keys;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/send")
public class Send extends AuthenticatedServlet {

    @Override
    protected void handle(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        checkCsrf(request);

        Keys receiverKeys = ServerRequest.to("/keys")
                .with("user", parameters.get("receiver"))
                .get(Keys.class);
        String encryptedBody = RSA.encrypt(parameters.get("body"), receiverKeys.getPubKey(), receiverKeys.getExponent());


        if (parameters.get("sign") != null) {
            Keys senderKeys = ServerRequest.to("/keys")
                    .with("token", parameters.get("token"))
                    .get(Keys.class);
            parameters.put("signature",
                    RSA.encrypt(new HashAlgorithm("SHA-256").hash(parameters.get("body")),
                            getPrivkey(),
                            senderKeys.getExponent())
            );
        }

        parameters.remove("privkey");
        parameters.put("body", encryptedBody);

        ServerRequest.to("/send").with(parameters).post();

        request.getRequestDispatcher("/home.jsp").forward(request,response);
    }
}
