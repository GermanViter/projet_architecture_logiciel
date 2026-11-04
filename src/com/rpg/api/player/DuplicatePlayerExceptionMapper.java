package com.rpg.api.player;

import com.rpg.domain.player.exceptions.DuplicatePlayerException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public final class DuplicatePlayerExceptionMapper implements ExceptionMapper<DuplicatePlayerException> {
    @Override
    public Response toResponse(DuplicatePlayerException exception) {
        return Response.status(Response.Status.CONFLICT)
                .type(MediaType.APPLICATION_JSON)
                .entity("{\"error\": \"" + exception.getMessage() + "\"}")
                .build();
    }
}
