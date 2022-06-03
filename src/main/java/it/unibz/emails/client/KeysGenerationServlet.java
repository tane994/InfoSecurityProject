package it.unibz.emails.client;

import it.unibz.emails.client.encryption.RSAKeys;
import it.unibz.emails.server.servlet.BaseServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/generateKeys")
public class KeysGenerationServlet extends BaseServlet {

    public KeysGenerationServlet() {
        super("email");
    }

    @Override
    protected void handle(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RSAKeys keys = RSAKeys.withRandomPrimes();

        db.insert("INSERT INTO privkeys(email,privkey) VALUES (?,?)", parameters.get("email"), keys.d());

        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        out.println("pubkey=" + keys.e() + ",exponent=" + keys.n());
        out.close();
    }
}
