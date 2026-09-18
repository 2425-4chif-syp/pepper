package at.htlleonding.pepper.model;

import java.util.Objects;

public class GameScoreId {

    private Long game;
    private Long person;

    //region constructors
    public GameScoreId() {
    }

    public GameScoreId(Long game, Long person) {
        this.game = game;
        this.person = person;
    }
    //endregion

    //region getters and setters
    public Long getGame() {
        return game;
    }

    public void setGame(Long game) {
        this.game = game;
    }

    public Long getPerson() {
        return person;
    }

    public void setPerson(Long person) {
        this.person = person;
    }
    //endregion

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        GameScoreId that = (GameScoreId) o;
        return Objects.equals(game, that.game) && Objects.equals(person, that.person);
    }

    @Override
    public int hashCode() {
        return Objects.hash(game, person);
    }
}
