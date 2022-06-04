package it.unibz.emails.client;

import it.unibz.emails.client.encryption.RSAKeys;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/register")
public class Register extends BaseServlet {

    @Override
    protected void handle(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        checkCsrf(request);
        ServerRequest.to("/register").with(parameters).post();
        RSAKeys keys = RSAKeys.withRandomPrimes();
        ServerRequest.to("/keys")
                .with("token", parameters.get("token"))
                .with("pubkey", String.valueOf(keys.e()))
                .with("exponent", String.valueOf(keys.n()))
                .post();

        request.getSession().setAttribute("email", parameters.get("email"));
        response.getWriter().println(
                "<html><head><script src='script.js' defer></script></head>" +
                "<body onload=\"savePrivkey('"+keys.d()+"','"+parameters.get("email")+"');\">" +
                "<h1>Registered Successfully</h1>" +
                "</body></html>"
        );
    }
}
