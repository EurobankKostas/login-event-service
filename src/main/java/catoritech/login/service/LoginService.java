package catoritech.login.service;

import catoritech.login.domain.dto.LoginRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.Valid;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@ApplicationScoped
public class LoginService {

    private final Emitter<LoginRequest> loginEventInputEmitter;

    public LoginService(@Channel("login-events-in") Emitter<LoginRequest> loginEventInputEmitter) {
        this.loginEventInputEmitter = loginEventInputEmitter;
    }

    public void sendLoginRequest(@Valid LoginRequest loginRequest) {
        loginEventInputEmitter.send(loginRequest);
    }
}
