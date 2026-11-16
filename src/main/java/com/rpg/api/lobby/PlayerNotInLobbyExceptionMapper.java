package com.rpg.api.lobby;

import com.rpg.api.ErrorResponse;
import com.rpg.domain.lobby.exceptions.PlayerNotInLobbyException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public final class PlayerNotInLobbyExceptionMapper
        implements ExceptionMapper<PlayerNotInLobbyException> {

    @Override
    public Response toResponse(PlayerNotInLobbyException exception) {
        return Response.status(Response.Status.NOT_FOUND)
                .type(MediaType.APPLICATION_JSON)
                .entity(new ErrorResponse("PLAYER_NOT_IN_LOBBY",
                        "Ce joueur n'est pas membre de ce lobby."))
                .build();
    }
}
