package at.htlleonding.pepper.service;

import at.htlleonding.pepper.model.Move;
import at.htlleonding.pepper.repository.MoveRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.util.List;

@ApplicationScoped
public class MoveService {

    @Inject
    MoveRepository moveRepository;

    public List<Move> getAll() {
        return moveRepository.listAll();
    }

    public Move findMove(Long id) {
        Move move = moveRepository.findById(id);
        if (move == null) {
            throw new NotFoundException("Move " + id + " not found");
        }
        return move;
    }

    @Transactional
    public Move create(Move move) {
        moveRepository.persist(move);
        return move;
    }

    @Transactional
    public void delete(Long id) {
        moveRepository.delete(findMove(id));
    }
}
