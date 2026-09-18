package at.htlleonding.pepper.service;

import at.htlleonding.pepper.dto.GameDto;
import at.htlleonding.pepper.dto.StepDto;
import at.htlleonding.pepper.dto.StepResponseDto;
import at.htlleonding.pepper.dto.TagAlongStoryDto;
import at.htlleonding.pepper.model.Game;
import at.htlleonding.pepper.model.GameType;
import at.htlleonding.pepper.model.Image;
import at.htlleonding.pepper.model.Move;
import at.htlleonding.pepper.model.Step;
import at.htlleonding.pepper.repository.GameRepository;
import at.htlleonding.pepper.repository.GameTypeRepository;
import at.htlleonding.pepper.repository.MoveRepository;
import at.htlleonding.pepper.repository.StepRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

import java.util.List;

@ApplicationScoped
public class TagAlongStoryService {

    private static final String TAG_ALONG_STORY = "TAG_ALONG_STORY";

    @Inject
    GameRepository gameRepository;

    @Inject
    GameTypeRepository gameTypeRepository;

    @Inject
    StepRepository stepRepository;

    @Inject
    MoveRepository moveRepository;

    @Inject
    ImageService imageService;

    public List<TagAlongStoryDto> getAll(Boolean withoutDisabled) {
        List<Game> games = Boolean.TRUE.equals(withoutDisabled)
                ? gameRepository.list("isEnabled = true and gameType.id = ?1", TAG_ALONG_STORY)
                : gameRepository.list("gameType.id = ?1", TAG_ALONG_STORY);
        return games.stream().map(this::toTagAlongStoryDto).toList();
    }

    public TagAlongStoryDto getById(Long id) {
        return toTagAlongStoryDto(findTagAlongStory(id));
    }

    public ImageService.Picture getStoryPicture(Long id) {
        Game game = findTagAlongStory(id);
        if (game.getStoryIcon() == null) {
            throw new NotFoundException("No image found for tag along story " + id);
        }
        ImageService.Picture picture = imageService.toPicture(game.getStoryIcon());
        if (picture == null) {
            throw new NotFoundException("No image found for tag along story " + id);
        }
        return picture;
    }

    @Transactional
    public Game create(GameDto gameDto) {
        if (gameDto == null) {
            throw new BadRequestException("Tag along story is required");
        }
        if (gameDto.icon() == null || gameDto.icon().isBlank()) {
            throw new BadRequestException("The icon of tag along story is required");
        }

        Game game = new Game();
        game.setName(gameDto.name());
        game.setEnabled(Boolean.TRUE.equals(gameDto.isEnabled()));
        game.setGameType(resolveGameType(gameDto.gameType()));

        Image image = imageService.storeImage(
                gameDto.icon(),
                null,
                null,
                "Bild fuer Mitmachgeschichte",
                "story"
        );
        game.setStoryIcon(image);
        gameRepository.persist(game);
        return game;
    }

    @Transactional
    public TagAlongStoryDto update(Long id, GameDto gameDto) {
        Game existingGame = findTagAlongStory(id);
        if (gameDto == null) {
            throw new BadRequestException("Tag along story update is required");
        }

        if (gameDto.name() != null) {
            existingGame.setName(gameDto.name());
        }
        if (gameDto.isEnabled() != null) {
            existingGame.setEnabled(gameDto.isEnabled());
        }
        if (gameDto.gameType() != null) {
            existingGame.setGameType(resolveGameType(gameDto.gameType()));
        }
        if (gameDto.icon() != null && !gameDto.icon().isBlank()) {
            Image oldIcon = existingGame.getStoryIcon();
            Image newIcon = imageService.storeImage(
                    gameDto.icon(),
                    null,
                    null,
                    "Bild fuer Mitmachgeschichte",
                    "story"
            );
            existingGame.setStoryIcon(newIcon);
            if (oldIcon != null) {
                imageService.deleteImage(oldIcon);
            }
        }

        return toTagAlongStoryDto(existingGame);
    }

    @Transactional
    public void delete(Long id) {
        Game game = findTagAlongStory(id);
        List<Step> steps = stepRepository.findByGameId(id);
        for (Step step : steps) {
            Image image = step.getImage();
            stepRepository.delete(step);
            if (image != null) {
                imageService.deleteImage(image);
            }
        }
        Image storyIcon = game.getStoryIcon();
        gameRepository.delete(game);
        if (storyIcon != null) {
            imageService.deleteImage(storyIcon);
        }
    }

    public List<StepResponseDto> getSteps(Long gameId) {
        findTagAlongStory(gameId);
        return stepRepository.findByGameId(gameId).stream()
                .map(this::toStepResponseDto)
                .toList();
    }

    @Transactional
    public Step createStep(Long gameId, StepDto stepDto) {
        if (stepDto == null) {
            throw new BadRequestException("Step is required");
        }

        Game game = findTagAlongStory(gameId);
        Step step = new Step();
        step.setGame(game);
        step.setIndex(stepDto.index());
        step.setText(stepDto.text());
        step.setDurationInSeconds(stepDto.durationInSeconds());
        step.setMove(resolveMove(stepDto.move()));

        if (stepDto.image() != null && !stepDto.image().isBlank()) {
            Image image = imageService.storeImage(
                    stepDto.image(),
                    null,
                    null,
                    stepDto.image_desc() != null ? stepDto.image_desc() : "Bild fuer Szene",
                    "story-step/" + gameId
            );
            step.setImage(image);
        }

        stepRepository.persist(step);
        return step;
    }

    @Transactional
    public Step deleteStep(Long gameId, Long stepId) {
        findTagAlongStory(gameId);
        Step step = stepRepository.findById(stepId);
        if (step == null) {
            throw new NotFoundException("Step " + stepId + " not found");
        }
        if (step.getGame() == null || !step.getGame().getId().equals(gameId)) {
            throw new BadRequestException("Step does not belong to game " + gameId);
        }

        Image image = step.getImage();
        stepRepository.delete(step);
        if (image != null) {
            imageService.deleteImage(image);
        }
        return step;
    }

    private Game findTagAlongStory(Long id) {
        Game game = gameRepository.find("id = ?1 and gameType.id = ?2", id, TAG_ALONG_STORY).firstResult();
        if (game == null) {
            throw new NotFoundException("Tag along story " + id + " not found");
        }
        return game;
    }

    private GameType resolveGameType(GameType requestedGameType) {
        String id = requestedGameType != null && requestedGameType.getId() != null
                ? requestedGameType.getId()
                : TAG_ALONG_STORY;
        GameType gameType = gameTypeRepository.findById(id);
        if (gameType == null) {
            throw new NotFoundException("Game type " + id + " not found");
        }
        return gameType;
    }

    private Move resolveMove(Move requestedMove) {
        if (requestedMove == null || requestedMove.getId() == null) {
            return null;
        }
        Move move = moveRepository.findById(requestedMove.getId());
        if (move == null) {
            throw new NotFoundException("Move " + requestedMove.getId() + " not found");
        }
        return move;
    }

    private TagAlongStoryDto toTagAlongStoryDto(Game game) {
        return new TagAlongStoryDto(
                game.getId(),
                game.getName(),
                imageService.toMetadataDto(game.getStoryIcon()),
                game.isEnabled(),
                game.getGameType()
        );
    }

    private StepResponseDto toStepResponseDto(Step step) {
        return new StepResponseDto(
                step.getId(),
                step.getIndex(),
                imageService.toMetadataDto(step.getImage()),
                step.getMove(),
                step.getText(),
                step.getDurationInSeconds()
        );
    }
}
