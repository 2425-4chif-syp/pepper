package at.htlleonding.pepper.service;

import at.htlleonding.pepper.dto.GameScoreDto;
import at.htlleonding.pepper.model.Game;
import at.htlleonding.pepper.model.GameScore;
import at.htlleonding.pepper.model.Person;
import at.htlleonding.pepper.repository.GameRepository;
import at.htlleonding.pepper.repository.GameScoreRepository;
import at.htlleonding.pepper.repository.PersonRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

import java.util.List;

@ApplicationScoped
public class GameScoreService {

    @Inject
    GameScoreRepository gameScoreRepository;

    @Inject
    GameRepository gameRepository;

    @Inject
    PersonRepository personRepository;

    public List<GameScore> getAll() {
        return gameScoreRepository.listAll();
    }

    public GameScore getById(Long gameId, Long playerId) {
        GameScore gameScore = gameScoreRepository.findByGameAndPlayer(gameId, playerId);
        if (gameScore == null) {
            throw new NotFoundException("Game score for game " + gameId + " and person " + playerId + " not found");
        }
        return gameScore;
    }

    public List<GameScore> getByGame(Long gameId) {
        return gameScoreRepository.findByGame(gameId);
    }

    public List<GameScore> getByPlayer(Long playerId) {
        return gameScoreRepository.findByPlayer(playerId);
    }

    public List<GameScore> getTopScores(int limit) {
        return gameScoreRepository.findTopScores(limit);
    }

    public List<GameScore> getLatestScores(int limit) {
        return gameScoreRepository.findLatestScores(limit);
    }

    @Transactional
    public GameScore create(GameScoreDto gameScoreDto) {
        GameScore gameScore = toEntity(gameScoreDto);
        gameScoreRepository.persist(gameScore);
        return gameScore;
    }

    @Transactional
    public GameScore update(Long gameId, Long playerId, GameScore updatedGameScore) {
        GameScore existingGameScore = getById(gameId, playerId);
        existingGameScore.setScore(updatedGameScore.getScore());
        existingGameScore.setDateTime(updatedGameScore.getDateTime());
        existingGameScore.setComment(updatedGameScore.getComment());
        existingGameScore.setElapsedTime(updatedGameScore.getElapsedTime());
        return existingGameScore;
    }

    @Transactional
    public void delete(Long gameId, Long playerId) {
        gameScoreRepository.delete(getById(gameId, playerId));
    }

    private GameScore toEntity(GameScoreDto gameScoreDto) {
        if (gameScoreDto == null || gameScoreDto.game() == null || gameScoreDto.game().id() == null
                || gameScoreDto.person() == null || gameScoreDto.person().id() == null) {
            throw new BadRequestException("game.id and person.id are required");
        }

        Game game = gameRepository.findById(gameScoreDto.game().id());
        if (game == null) {
            throw new NotFoundException("Game " + gameScoreDto.game().id() + " not found");
        }

        Person person = personRepository.findById(gameScoreDto.person().id());
        if (person == null) {
            throw new NotFoundException("Person " + gameScoreDto.person().id() + " not found");
        }

        GameScore gameScore = new GameScore();
        gameScore.setGame(game);
        gameScore.setPerson(person);
        gameScore.setComment(gameScoreDto.comment());
        gameScore.setDateTime(gameScoreDto.dateTime());
        gameScore.setElapsedTime(gameScoreDto.elapsedTime());
        gameScore.setScore(gameScoreDto.score());
        return gameScore;
    }
}
