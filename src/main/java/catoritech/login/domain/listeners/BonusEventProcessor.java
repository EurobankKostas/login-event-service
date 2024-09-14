package catoritech.login.domain.listeners;

import catoritech.login.domain.dto.BonusEventProcess;
import catoritech.login.service.BonusService;
import catoritech.login.service.EventService;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@ApplicationScoped
@Slf4j
@RequiredArgsConstructor
public class BonusEventProcessor {

    private final EventService eventService;

    private final BonusService bonusService;

    @ConsumeEvent("bonus-events-process")
    public Uni<Void> processBonusEvent(BonusEventProcess bonusEventProcess) {
        log.debug("#processBonusEvent: bonusEventProcess={}", bonusEventProcess);
        UUID eventId = bonusEventProcess.getEventId();

        return eventService.checkIfBonusEventShouldBeSkipped(eventId)
                .flatMap(skip -> {
                    if (skip) {
                        log.warn("eventId={} already processed, skipping...", eventId);
                        return Uni.createFrom().voidItem();
                    }
                    return bonusService.updatePlayerBonus(bonusEventProcess, eventId);
                })
                .replaceWithVoid();
    }
}
