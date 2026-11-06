package com.rpg.api.session;

import com.rpg.application.session.LoginCommand;
import com.rpg.application.session.LoginOutput;
import com.rpg.application.session.LoginUseCase;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/sessions")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public final class SessionResource {
    private final LoginUseCase login;

    @Inject
    public SessionResource(LoginUseCase login) {
        this.login = login;
    }

    @POST
    public Response login(@Valid @NotNull LoginRequest request) {
        LoginOutput output = login.execute(new LoginCommand(request.email, request.password));
        return Response.ok(output).build();
    }
}
