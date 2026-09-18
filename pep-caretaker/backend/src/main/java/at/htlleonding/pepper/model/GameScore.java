package at.htlleonding.pepper.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@IdClass(GameScoreId.class)
@Table(name = "pe_game_score")
public class GameScore {
    @Id
    @ManyToOne
    @JoinColumn(name = "gs_g_id")
    private Game game;

    @Id
    @ManyToOne
    @JoinColumn(name = "gs_p_id", nullable = true)
    private Person person;

    @Column(name = "gs_date_time")
    private LocalDateTime dateTime;

    @Column(name = "gs_score")
    private int score;

    @Column(name = "gs_comment")
    private String comment;

    @Column(name = "gs_elapsed_time")
    private int elapsedTime;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(int elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
