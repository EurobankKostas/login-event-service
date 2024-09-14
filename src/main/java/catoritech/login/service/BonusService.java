package catoritech.login.service;

import catoritech.login.domain.PlayerBonus;
import catoritech.login.domain.Event;
import catoritech.login.domain.dto.BonusEventProcess;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.LockModeType;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.reactive.mutiny.Mutiny;

import java.util.UUID;

@ApplicationScoped
@Slf4j
public class BonusService {
    private final Mutiny.SessionFactory sessionFactory;

    public BonusService(Mutiny.SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @WithTransaction
    public Uni<Void> persistPlayerBonusWithAuditing(BonusEventProcess bonusEventProcess, UUID eventId) {
        return PlayerBonus.<PlayerBonus>find("userId", bonusEventProcess.getUserId())
                .firstResult()
                .onItem()
                .ifNull()
                .failWith(() -> new IllegalStateException("player not found for eventId=" + eventId))
                .flatMap(playerBonusToUpdate ->
                        PlayerBonus.update("totalBonus = ?1 where id = ?2", bonusEventProcess.getBonusAmount(), playerBonusToUpdate.getId())
                                .flatMap(updatedCount -> {
                                    if (updatedCount == 0) {
                                        return Uni.createFrom().failure(
                                                new IllegalStateException("update failed for playerBonus with eventId=" + eventId)
                                        );
                                    }
                                    return Uni.createFrom().item(playerBonusToUpdate);
                                })
                )
                .flatMap(updatedPlayerBonus -> updateEventToProcessed(eventId)
                        .invoke(() -> log.debug("successfully audited event {} and persisted playerBonus {}", updatedPlayerBonus, eventId))
                        .replaceWithVoid());
    }

    public Uni<Boolean> checkIfLoginEventShouldBeSkipped(UUID eventId) {
        return sessionFactory.withTransaction((session, tx) ->
                session.find(Event.class, eventId, LockModeType.PESSIMISTIC_WRITE)
                        .flatMap(event -> {
                            if (event == null) {
                                // No event found, create a new one.
                                return persistProcessedEvent(eventId, false, false)
                                        .replaceWith(false);
                            } else {
                                // Log and skip if the event is either locked or processed.
                                log.debug("skipping event for eventId={}, Locked={}, Processed={}", eventId, event.isLocked(), event.isProcessed());
                                return Uni.createFrom()
                                        .item(true);
                            }
                        })
        );
    }

    public Uni<Void> persistProcessedEvent(UUID eventId, boolean locked, boolean processed) {
        log.debug("Persisting processed event for eventId={}", eventId);
        Event event = new Event(eventId, locked, processed);
        return sessionFactory
                .withTransaction((session) -> session.persist(event).call(session::flush))
                .onFailure().invoke(th -> log.error("failed to persist event: " + th.getMessage(), th));
    }

    public Uni<Integer> updateEventToProcessed(UUID eventId) {
        log.debug("Persisting processed event for eventId={}", eventId);
        return Event.update("UPDATE Event SET processed = true, locked = false WHERE id = ?1", eventId)
                .onItem()
                .invoke(count -> log.debug("updated {} rows for eventId={}", count, eventId));
    }
}
