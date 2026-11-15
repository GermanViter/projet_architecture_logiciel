package com.rpg.api.session;

import com.rpg.domain.session.exceptions.InvalidSessionException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public final class InvalidSessionExceptionMapper
        implements ExceptionMapper<InvalidSessionException> {

    @Override
    public Response toResponse(InvalidSessionException exception) {
        return Response.status(Response.Status.UNAUTHORIZED)
                .type(MediaType.APPLICATION_JSON)
                .entity("{\"error\": \"" + exception.getMessage() + "\"}")
                .build();
    }
}
