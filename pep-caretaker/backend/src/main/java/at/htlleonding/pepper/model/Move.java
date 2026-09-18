package at.htlleonding.pepper.model;

import jakarta.persistence.*;

@Entity
@Table(name = "pe_move")
public class Move {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "m_id")
    private Long id;

    @Column(name = "m_name")
    private String name;

    @Column(name = "m_description")
    private String description;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
