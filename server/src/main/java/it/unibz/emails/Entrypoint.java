package it.unibz.emails;

import it.unibz.emails.exceptions.InvalidParameterException;
import it.unibz.emails.entities.Token;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;

@ApplicationPath("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
public class Entrypoint extends Application {
    public static String getEmailFrom(String token) {
        Token email = Token.getEmailFrom(token);
        if (email == null) throw new InvalidParameterException("Invalid token");
        else return email.get();
    }
}
