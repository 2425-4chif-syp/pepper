package at.htlleonding.pepper.repository;

import at.htlleonding.pepper.model.Move;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MoveRepository implements PanacheRepository<Move> {

}
