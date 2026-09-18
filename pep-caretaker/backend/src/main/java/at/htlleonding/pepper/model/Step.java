package at.htlleonding.pepper.model;

import jakarta.persistence.*;

@Entity
@Table(name = "pe_step")
public class Step {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "st_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "st_g_id")
    private Game game;

    @Column(name = "st_index")
    private int index;

    @ManyToOne
    @JoinColumn(name = "st_i_id")
    private Image image;

    @ManyToOne
    @JoinColumn(name = "st_m_id")
    private Move move;

    @Column(name = "st_text")
    private String text;

    @Column(name = "st_duration_in_sec")
    private int durationInSeconds;

    public Step() {
    }

    public Step(
            Long id,
            Game game,
            int index,
            Image image,
            Move move,
            String text,
            int durationInSeconds) {
        this.id = id;
        this.game = game;
        this.index = index;
        this.image = image;
        this.move = move;
        this.text = text;
        this.durationInSeconds = durationInSeconds;
    }

    //region getter and setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Move getMove() {
        return move;
    }

    public void setMove(Move move) {
        this.move = move;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getDurationInSeconds() {
        return durationInSeconds;
    }

    public void setDurationInSeconds(int durationInSeconds) {
        this.durationInSeconds = durationInSeconds;
    }
    //endregion
}
