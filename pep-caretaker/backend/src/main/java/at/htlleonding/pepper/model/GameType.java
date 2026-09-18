package at.htlleonding.pepper.model;

import jakarta.persistence.*;

@Entity
@Table(name="pe_game_type")
public class GameType {

    @Id
    @Column(name = "gt_id")
    private String id; // zB MEMORY oder TAG_ALONG_STORY

    @Column(name = "gt_name")
    private String name; // zB Memory oder Mitmachgeschichten

    //region Constructors
    public GameType() {
    }

    public GameType(String name) {
        this.name = name;
    }
    //endregion

    //region getter and setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    //endregion


    @Override
    public String toString() {
        return "GameType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
