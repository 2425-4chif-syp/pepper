package at.htlleonding.pepper.model;

import jakarta.persistence.*;

/**
 * Image metadata. The binary itself lives in MinIO (bucket pep-caretaker-images),
 * referenced by {@link #objectKey}.
 */
@Entity
@Table(name = "pe_image")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "i_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "i_p_id")
    private Person person;

    @Column(name = "i_object_key")
    private String objectKey;

    @Column(name = "i_content_type")
    private String contentType;

    @Column(name = "i_url")
    private String url;

    @Column(name = "i_description")
    private String description;

    public Image() {
    }

    public Image(Person person, String objectKey, String contentType, String url, String description) {
        this.person = person;
        this.objectKey = objectKey;
        this.contentType = contentType;
        this.url = url;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
