package catoritech.login.service;

import catoritech.login.domain.PlayerBonus;
import catoritech.login.domain.dto.BonusEventProcess;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@ApplicationScoped
@Slf4j
@RequiredArgsConstructor
public class BonusService {

    private final EventService eventService;

    @WithTransaction
    public Uni<Void> updatePlayerBonus(BonusEventProcess bonusEventProcess, UUID eventId) {
        return PlayerBonus.<PlayerBonus>find("userId", bonusEventProcess.getUserId())
                .firstResult()
                .onItem()
                .ifNull()
                .failWith(() -> new IllegalStateException("playerBonus not found for userId=" + bonusEventProcess.getUserId()))
                .flatMap(playerBonusToUpdate ->
                        PlayerBonus.update("totalBonus = ?1 where id = ?2", bonusEventProcess.getBonusAmount(), playerBonusToUpdate.getId())
                                .flatMap(updatedCount -> {
                                    if (updatedCount == 0) {
                                        return Uni.createFrom().failure(
                                                new IllegalStateException("update failed for playerBonus with userId=" + bonusEventProcess.getUserId())
                                        );
                                    }
                                    return Uni.createFrom().item(playerBonusToUpdate);
                                })
                )
                .onFailure()
                .invoke(throwable -> log.error("exception occurred {}", throwable.getMessage(), throwable))
                .eventually(() -> eventService.updateEventToProcessed(eventId))
                .replaceWithVoid();
    }
}
