package it.unibz.emails.endpoints;

import it.unibz.emails.Entrypoint;
import it.unibz.emails.exceptions.InvalidParameterException;
import it.unibz.emails.entities.Mail;
import it.unibz.emails.entities.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.hibernate.validator.constraints.Length;

@Path("/send")
public class Send {
    @POST
    public void send(@NotBlank @Length(min=5) @Email @FormParam("receiver") String receiver,
                     @NotBlank @Length(min=4) @FormParam("subject") String subject,
                     @NotBlank @Length(min=4) @FormParam("body") String body,
                     @FormParam("signature") String signature,
                     @NotBlank @HeaderParam("X-Token") String token
    ) {
        User receiverObj = User.get(receiver);
        if (receiverObj == null) throw new InvalidParameterException("Receiver not found");
        if (signature == null) signature  = "";

        User sender = User.get(Entrypoint.getEmailFrom(token));
        Mail.send(sender, receiverObj, subject, body, signature);
    }
}
