package catoritech.login.kafka.producer;

import catoritech.login.domain.dto.LoginRequest;
import catoritech.login.domain.events.LoginEvent;
import jakarta.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import io.smallrye.reactive.messaging.kafka.Record;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import java.time.Instant;
import java.util.UUID;

@ApplicationScoped
public class LoginEventProducer {

    @Incoming("login-events-in")
    @Outgoing("login-events-out")
    public Record<UUID, LoginEvent> sendLoginEvent(LoginRequest loginRequest) {
        return Record.of(UUID.randomUUID(), new LoginEvent(
                loginRequest.getUserId(),
                Instant.now()
        ));
    }
}
