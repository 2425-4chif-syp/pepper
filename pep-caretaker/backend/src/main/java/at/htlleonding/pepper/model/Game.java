package at.htlleonding.pepper.model;

import jakarta.persistence.*;

@Entity
@Table(name = "pe_game")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "g_id")
    private Long id;

    @Column(name = "g_name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "g_story_icon")
    private Image storyIcon;

    @Column(name = "g_is_enabled")
    private boolean isEnabled;

    @ManyToOne
    @JoinColumn(name = "g_gt_id")
    private GameType gameType;


    //region constructors
    public Game() {
    }

    public Game(String name, Image storyIcon, boolean isEnabled, GameType gameType) {
        this.name = name;
        this.storyIcon = storyIcon;
        this.isEnabled = isEnabled;
        this.gameType = gameType;
    }
    //endregion

    //region getter and setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Image getStoryIcon() {
        return storyIcon;
    }

    public void setStoryIcon(Image storyIcon) {
        this.storyIcon = storyIcon;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public GameType getGameType() {
        return gameType;
    }

    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }

    //endregion
}
