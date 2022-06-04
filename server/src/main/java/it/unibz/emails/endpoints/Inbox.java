package it.unibz.emails.endpoints;

import it.unibz.emails.Entrypoint;
import it.unibz.emails.entities.Mail;
import it.unibz.emails.entities.MailList;
import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;

@Path("/inbox")
public class Inbox {
    @GET
    public MailList inbox(@QueryParam("search") String search,
                          @NotBlank @HeaderParam("X-Token") String token
    ) {
        return new MailList(Mail.inboxFor(Entrypoint.getEmailFrom(token), search));
    }
}
