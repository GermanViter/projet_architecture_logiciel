package com.rpg.api.player;

import com.rpg.domain.player.exceptions.PlayerNotFoundException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public final class PlayerNotFoundExceptionMapper implements ExceptionMapper<PlayerNotFoundException> {
    @Override
    public Response toResponse(PlayerNotFoundException exception) {
        return Response.status(Response.Status.NOT_FOUND)
                .type(MediaType.APPLICATION_JSON)
                .entity("{\"error\": \"" + exception.code() + "\"}")
                .build();
    }
}
