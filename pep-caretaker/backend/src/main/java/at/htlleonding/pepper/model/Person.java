package at.htlleonding.pepper.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "pe_person")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "p_id")
    private Long id;

    @Column(name = "p_first_name")
    private String firstName;

    @Column(name = "p_last_name")
    private String lastName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(name = "p_dob")
    private LocalDate dob;

    @Column(name = "p_room_no")
    private String roomNo;

    @Column(name = "p_isWorker")
    private Boolean isWorker;

    // accepted on input (login/create) but never serialized back to clients
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "p_password")
    private String password;

    @Column(name = "p_gender")
    private Boolean gender;



    public Person() {}

    @JsonCreator
    public Person(
            @JsonProperty("firstName") String firstName,
            @JsonProperty("lastName") String lastName,
            @JsonProperty("dob") LocalDate dob,
            @JsonProperty("roomNo") String roomNo,
            @JsonProperty("isWorker") Boolean isWorker,
            @JsonProperty("password") String password
    ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
        this.roomNo = roomNo;
        this.isWorker = isWorker;
        this.password = password;
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }

    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }

    public void setLastName(String lastName) { this.lastName = lastName; }

    public LocalDate getDob() { return dob; }

    public void setDob(LocalDate dob) { this.dob = dob; }

    public String getRoomNo() { return roomNo; }

    public void setRoomNo(String roomNo) { this.roomNo = roomNo; }

    public Boolean getIsWorker() { return isWorker; }

    public void setIsWorker(Boolean isWorker) { this.isWorker = isWorker; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public Boolean getGender() {
        return gender;
    }

    public void setGender(Boolean gender) {
        this.gender = gender;
    }
}
