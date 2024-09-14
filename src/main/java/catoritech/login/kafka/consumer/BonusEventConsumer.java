package catoritech.login.kafka.consumer;

import catoritech.login.domain.dto.BonusEventProcess;
import catoritech.login.domain.events.BonusEvent;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.kafka.KafkaRecord;
import io.vertx.mutiny.core.eventbus.EventBus;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Acknowledgment;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import java.util.UUID;

@ApplicationScoped
@Slf4j
@RequiredArgsConstructor
public class BonusEventConsumer {

    private final EventBus eventBus;

    @Incoming("bonus-events-in")
    @Acknowledgment(Acknowledgment.Strategy.POST_PROCESSING)
    public Uni<Void> processBonusEvent(KafkaRecord<UUID, BonusEvent> eventIdToBonusEvent) {
        log.debug("#processBonusEvent: eventId={}", eventIdToBonusEvent.getKey());
        BonusEventProcess processEvent = new BonusEventProcess(
                eventIdToBonusEvent.getPayload(),
                eventIdToBonusEvent.getKey()
        );
        eventBus.send("bonus-events-process", processEvent);
        return Uni.createFrom()
                .voidItem()
                .replaceWithVoid();
    }
}
