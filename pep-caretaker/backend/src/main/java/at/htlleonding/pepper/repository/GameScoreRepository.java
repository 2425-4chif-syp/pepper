package at.htlleonding.pepper.repository;

import at.htlleonding.pepper.model.GameScore;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class GameScoreRepository implements PanacheRepository<GameScore> {

    // Find a game score by composite key (game.id, person.id)
    public GameScore findByGameAndPlayer(Long gameId, Long playerId) {
        return find("game.id = ?1 and person.id = ?2", gameId, playerId).firstResult();
    }

    // Find all scores for a specific game
    public List<GameScore> findByGame(Long gameId) {
        return list("game.id", gameId);
    }

    // Find all scores for a specific player
    public List<GameScore> findByPlayer(Long playerId) {
        return list("person.id", playerId);
    }

    // Find top N scores (highest first)
    public List<GameScore> findTopScores(int limit) {
        return find("ORDER BY score DESC").page(0, limit).list();
    }

    // Find latest N scores (most recent first)
    public List<GameScore> findLatestScores(int limit) {
        return find("ORDER BY dateTime DESC").page(0, limit).list();
    }
}
