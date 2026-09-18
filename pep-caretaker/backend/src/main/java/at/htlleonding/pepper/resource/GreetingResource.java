package at.htlleonding.pepper.resource;

import at.htlleonding.pepper.service.SmallTalkService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/chat")
public class GreetingResource {

    @Inject
    SmallTalkService smallTalkService;

    @POST
    @RolesAllowed({"admin", "caretaker", "resident"})
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String chat(String input) {
        return smallTalkService.chat(input);
    }
}
