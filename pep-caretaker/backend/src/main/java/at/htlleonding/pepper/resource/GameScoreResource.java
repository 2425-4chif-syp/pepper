package at.htlleonding.pepper.resource;

import at.htlleonding.pepper.dto.GameScoreDto;
import at.htlleonding.pepper.model.GameScore;
import at.htlleonding.pepper.service.GameScoreService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("gamescore")
@RolesAllowed({"admin", "caretaker", "resident"})
public class GameScoreResource {

    @Inject
    GameScoreService gameScoreService;

    @GET
    public Response getGameScores() {
        return Response.ok(gameScoreService.getAll()).build();
    }

    @GET
    @Path("{gameId}/{playerId}")
    public Response getGameScoreById(@PathParam("gameId") Long gameId, @PathParam("playerId") Long playerId) {
        return Response.ok(gameScoreService.getById(gameId, playerId)).build();
    }

    @GET
    @Path("game/{gameId}")
    public Response getScoresByGame(@PathParam("gameId") Long gameId) {
        return Response.ok(gameScoreService.getByGame(gameId)).build();
    }

    @GET
    @Path("player/{playerId}")
    @RolesAllowed({"admin", "caretaker", "resident", "robot"})
    public Response getScoresByPlayer(@PathParam("playerId") Long playerId) {
        return Response.ok(gameScoreService.getByPlayer(playerId)).build();
    }

    @GET
    @Path("top")
    public Response getTopScores(@QueryParam("limit") @DefaultValue("10") int limit) {
        return Response.ok(gameScoreService.getTopScores(limit)).build();
    }

    @GET
    @Path("latest")
    public Response getLatestScores(@QueryParam("limit") @DefaultValue("10") int limit) {
        return Response.ok(gameScoreService.getLatestScores(limit)).build();
    }

    @POST
    @RolesAllowed({"admin", "caretaker", "resident", "robot"})
    public Response createGameScore(GameScoreDto gameScoreDto) {
        return Response.status(Response.Status.CREATED)
                .entity(gameScoreService.create(gameScoreDto))
                .build();
    }

    @PUT
    @Path("{gameId}/{playerId}")
    public Response updateGameScore(@PathParam("gameId") Long gameId,
                                    @PathParam("playerId") Long playerId,
                                    GameScore updatedGameScore) {
        return Response.ok(gameScoreService.update(gameId, playerId, updatedGameScore)).build();
    }

    @DELETE
    @Path("{gameId}/{playerId}")
    public Response deleteGameScore(@PathParam("gameId") Long gameId, @PathParam("playerId") Long playerId) {
        gameScoreService.delete(gameId, playerId);
        return Response.noContent().build();
    }
}
