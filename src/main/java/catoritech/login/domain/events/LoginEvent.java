package catoritech.login.domain.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class LoginEvent {

    private UUID userId;

    private Instant timestamp;
}
