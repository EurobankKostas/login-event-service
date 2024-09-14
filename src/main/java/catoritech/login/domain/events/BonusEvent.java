package catoritech.login.domain.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class BonusEvent {

    private UUID userId;

    private BigDecimal bonusAmount;
}
