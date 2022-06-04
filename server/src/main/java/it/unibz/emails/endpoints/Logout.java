package it.unibz.emails.endpoints;

import it.unibz.emails.entities.Token;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("/logout")
public class Logout {
    @POST
    public void logout(@HeaderParam("X-Token") String token) {
        Token.delete(token);
    }
}
