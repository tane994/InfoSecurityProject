package it.unibz.emails.endpoints;


import it.unibz.emails.exceptions.NotFoundException;
import it.unibz.emails.entities.Token;
import it.unibz.emails.entities.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("/login")
public class Login {
    @POST
    public void login(@NotBlank @FormParam("email") String email,
                      @NotBlank @FormParam("password") String password,
                      @NotBlank @HeaderParam("X-Token") String token
    ) {
        System.out.println(email);
        User user = User.get(email, password);
        if (user == null) throw new NotFoundException("user not found");
        else Token.set(email, token);
    }
}
