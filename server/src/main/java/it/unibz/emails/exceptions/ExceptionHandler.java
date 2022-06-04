package it.unibz.emails.exceptions;

import it.unibz.emails.entities.ErrorMessage;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class ExceptionHandler implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception e) {
        Response.Status status = Response.Status.INTERNAL_SERVER_ERROR;

        if (e instanceof NullPointerException || e instanceof IllegalArgumentException || e instanceof InvalidParameterException)
            status = Response.Status.BAD_REQUEST;
        else if (e instanceof NotFoundException)
            status = Response.Status.NOT_FOUND;

        return Response.status(status).entity(new ErrorMessage(e)).build();
    }
}
