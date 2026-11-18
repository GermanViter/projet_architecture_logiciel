package com.rpg.api.lobby;

import com.rpg.api.BearerToken;
import com.rpg.application.lobby.CreateLobbyCommand;
import com.rpg.application.lobby.CreateLobbyUseCase;
import com.rpg.application.lobby.GetLobbyDetailsOutput;
import com.rpg.application.lobby.GetLobbyDetailsUseCase;
import com.rpg.application.lobby.JoinLobbyCommand;
import com.rpg.application.lobby.JoinLobbyOutput;
import com.rpg.application.lobby.JoinLobbyUseCase;
import com.rpg.application.lobby.LeaveLobbyCommand;
import com.rpg.application.lobby.LeaveLobbyUseCase;
import com.rpg.application.lobby.ListLobbiesOutput;
import com.rpg.application.lobby.ListLobbiesUseCase;
import com.rpg.application.lobby.LobbyOutput;
import com.rpg.application.lobby.SelectHeroCommand;
import com.rpg.application.lobby.SelectHeroUseCase;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

@Path("/lobbies")
@Produces(MediaType.APPLICATION_JSON)
public final class LobbyResource {
    private static final String LOBBY_PLAYERS_PATH = "{lobbyId}/players";
    private static final String PLAYER_ID_PATH = "{playerId}";
    private static final String HERO_PATH = "hero";

    private final CreateLobbyUseCase createLobby;
    private final JoinLobbyUseCase joinLobby;
    private final LeaveLobbyUseCase leaveLobby;
    private final ListLobbiesUseCase listLobbies;
    private final GetLobbyDetailsUseCase getLobbyDetails;
    private final SelectHeroUseCase selectHero;

    @Inject
    public LobbyResource(CreateLobbyUseCase createLobby, JoinLobbyUseCase joinLobby,
                         LeaveLobbyUseCase leaveLobby, ListLobbiesUseCase listLobbies,
                         GetLobbyDetailsUseCase getLobbyDetails, SelectHeroUseCase selectHero) {
        this.createLobby = createLobby;
        this.joinLobby = joinLobby;
        this.leaveLobby = leaveLobby;
        this.listLobbies = listLobbies;
        this.getLobbyDetails = getLobbyDetails;
        this.selectHero = selectHero;
    }

    @POST
    public Response createLobby(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorization) {
        LobbyOutput output =
                createLobby.execute(new CreateLobbyCommand(BearerToken.extractFrom(authorization)));

        return Response
                .created(UriBuilder.fromResource(LobbyResource.class)
                        .path(output.id())
                        .build())
                .entity(output)
                .build();
    }

    @POST
    @Path(LOBBY_PLAYERS_PATH)
    public Response joinLobby(@PathParam("lobbyId") String lobbyId,
                              @HeaderParam(HttpHeaders.AUTHORIZATION) String authorization) {
        JoinLobbyOutput output =
                joinLobby.execute(new JoinLobbyCommand(BearerToken.extractFrom(authorization), lobbyId));

        return Response
                .created(UriBuilder.fromResource(LobbyResource.class)
                        .path(LOBBY_PLAYERS_PATH)
                        .path(PLAYER_ID_PATH)
                        .build(output.lobby().id(), output.playerId()))
                .entity(output.lobby())
                .build();
    }

    @DELETE
    @Path(LOBBY_PLAYERS_PATH + "/" + PLAYER_ID_PATH)
    public Response leaveLobby(@PathParam("lobbyId") String lobbyId,
                               @PathParam("playerId") String playerId,
                               @HeaderParam(HttpHeaders.AUTHORIZATION) String authorization) {
        leaveLobby.execute(
                new LeaveLobbyCommand(BearerToken.extractFrom(authorization), lobbyId, playerId));

        return Response.noContent().build();
    }

    @PUT
    @Path(LOBBY_PLAYERS_PATH + "/" + PLAYER_ID_PATH + "/" + HERO_PATH)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response selectHero(@PathParam("lobbyId") String lobbyId,
                               @PathParam("playerId") String playerId,
                               @HeaderParam(HttpHeaders.AUTHORIZATION) String authorization,
                               @Valid @NotNull SelectHeroRequest request) {
        LobbyOutput output = selectHero.execute(new SelectHeroCommand(
                BearerToken.extractFrom(authorization), lobbyId, playerId, request.heroName));

        return Response.ok(output).build();
    }

    @GET
    public Response listLobbies() {
        ListLobbiesOutput output = listLobbies.execute();
        return Response.ok(output).build();
    }

    @GET
    @Path("{lobbyId}")
    public Response getLobbyDetails(@PathParam("lobbyId") String lobbyId) {
        GetLobbyDetailsOutput output = getLobbyDetails.execute(lobbyId);
        return Response.ok(output).build();
    }
}
