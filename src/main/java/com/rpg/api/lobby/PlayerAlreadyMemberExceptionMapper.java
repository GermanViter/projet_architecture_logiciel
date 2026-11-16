package com.rpg.api.lobby;

import com.rpg.api.ErrorResponse;
import com.rpg.domain.lobby.exceptions.PlayerAlreadyMemberException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public final class PlayerAlreadyMemberExceptionMapper
        implements ExceptionMapper<PlayerAlreadyMemberException> {

    @Override
    public Response toResponse(PlayerAlreadyMemberException exception) {
        return Response.status(Response.Status.CONFLICT)
                .type(MediaType.APPLICATION_JSON)
                .entity(new ErrorResponse("ALREADY_MEMBER", "Vous êtes déjà membre de ce lobby."))
                .build();
    }
}
