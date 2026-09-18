package at.htlleonding.pepper.repository;

import at.htlleonding.pepper.model.Step;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class StepRepository implements PanacheRepository<Step> {
    public List<Step> findByGameId(Long gameId) {
        return find("game.id", gameId).list();
    }
}
