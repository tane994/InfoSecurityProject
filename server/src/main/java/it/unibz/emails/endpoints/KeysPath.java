package it.unibz.emails.endpoints;

import it.unibz.emails.Entrypoint;
import it.unibz.emails.exceptions.NotFoundException;
import it.unibz.emails.entities.Keys;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.*;

@Path("/keys")
public class KeysPath {
    @POST
    public void setKeys(@Positive @FormParam("pubkey") Integer pubkey,
                        @Positive @FormParam("exponent") Integer exponent,
                        @NotBlank @HeaderParam("X-Token") String token
    ) {
        Keys.set(Entrypoint.getEmailFrom(token), pubkey, exponent);
    }

    @GET
    public Keys get(@QueryParam("user") String user,
                    @HeaderParam("X-Token") String token
    ) {
        if (user == null) user = Entrypoint.getEmailFrom(token);
        Keys keys = Keys.get(user);
        if (keys == null) throw new NotFoundException("user not found");

        return keys;
    }
}
