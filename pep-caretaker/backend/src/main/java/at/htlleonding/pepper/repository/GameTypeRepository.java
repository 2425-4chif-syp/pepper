package at.htlleonding.pepper.repository;

import at.htlleonding.pepper.model.GameType;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GameTypeRepository implements PanacheRepositoryBase<GameType, String> {
}
