package catoritech.login.web;

import catoritech.login.domain.dto.LoginRequest;
import catoritech.login.service.LoginService;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

@Path("/v1/login-event")
@RequiredArgsConstructor
public class LoginResource {

    private final LoginService loginService;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response sendLoginEvent(LoginRequest loginRequest) {
        loginService.sendLoginRequest(loginRequest);
        return Response.ok().build();
    }
}
