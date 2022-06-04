package it.unibz.emails.endpoints;

import it.unibz.emails.exceptions.InvalidParameterException;
import it.unibz.emails.entities.Token;
import it.unibz.emails.entities.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.hibernate.validator.constraints.Length;

@Path("/register")
public class Register {
    @POST
    public void register(@NotBlank @Length(min=3) @FormParam("name") String name,
                         @NotBlank @Length(min=3) @FormParam("surname") String surname,
                         @NotBlank @Length(min=5) @Email @FormParam("email") String email,
                         @NotBlank @Length(min=8) @FormParam("password") String password,
                         @NotBlank @HeaderParam("X-Token") String token
    ) {
        boolean alreadyRegistered = User.get(email) != null;
        if (alreadyRegistered)
            throw new InvalidParameterException("Email already present");

        User.set(name, surname, email, password);
        Token.set(email, token);
    }
}
