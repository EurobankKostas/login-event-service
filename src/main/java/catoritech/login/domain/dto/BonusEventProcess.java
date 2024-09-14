package catoritech.login.domain.dto;

import catoritech.login.domain.events.BonusEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@ToString
public class BonusEventProcess extends BonusEvent {

    private UUID eventId;

    public BonusEventProcess(BonusEvent bonusEvent, UUID eventId) {
        super(bonusEvent.getUserId(), bonusEvent.getBonusAmount());
        this.eventId = eventId;
    }
}
