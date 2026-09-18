package at.htlleonding.pepper.model;

import at.htlleonding.pepper.resource.PersonResource;
import at.htlleonding.pepper.dto.PersonDto;
import at.htlleonding.pepper.repository.PersonRepository;
import io.quarkus.logging.Log;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotAuthorizedException;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.time.LocalDate;
import java.util.List;

import static jakarta.ws.rs.core.Response.Status.OK;
import static jakarta.ws.rs.core.Response.Status.UNAUTHORIZED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@QuarkusTest
@TestSecurity(user = "test-admin", roles = {"admin", "caretaker", "resident"})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PersonResourceTest {
    
    @Inject
    PersonResource personResource;

    @Inject
    PersonRepository personRepository;

    @Test
    @Order(130)
    @Transactional
    void getAllPeople_shouldReturnSList_whenNotEmpty() {

        Person person = new Person("Amir", "Mohammadi", LocalDate.of(2000, 2, 3), "101", false, null);
        personRepository.persist(person);


        var response = personResource.getAllPeople();
        Log.info(response.getEntity());


        assertThat(response.getStatus()).isEqualTo(OK.getStatusCode());
        List<?> persons = (List<?>) response.getEntity();
        assertThat(persons).isNotEmpty();
    }

    @Test
    @Order(140)
    @Transactional
    void getPersonById_shouldReturnCorrectPerson() {
        // arrange
        Person p = new Person("Tom", "Tester", LocalDate.of(1970, 5, 15), "Z1", false, null);
        personRepository.persist(p);
        Long id = p.getId();


        var response = personResource.getPersonById(id);
        Log.info(response.getEntity());


        assertThat(response.getStatus()).isEqualTo(OK.getStatusCode());
        assertThat(response.getEntity()).isInstanceOf(PersonDto.class);
        PersonDto dto = (PersonDto) response.getEntity();
        assertThat(dto.firstName()).isEqualTo("Tom");
        assertThat(dto.lastName()).isEqualTo("Tester");
    }

    @Test
    @Order(150)
    @Transactional
    void updatePerson_shouldModifyFieldsCorrectly() {
        // arrange
        Person p = new Person("Karl", "Alt", LocalDate.of(1965, 4, 12), "A1", true, null);
        personRepository.persist(p);

        Person updated = new Person("Karlheinz", "Alt", null, "B2", true, null);

        // act
        var response = personResource.updatePerson(p.getId(), updated);
        Log.info(response.getEntity());

        // assert
        assertThat(response.getStatus()).isEqualTo(OK.getStatusCode());
        Person result = (Person) response.getEntity();
        assertThat(result.getFirstName()).isEqualTo("Karlheinz");
        assertThat(result.getRoomNo()).isEqualTo("B2");
    }

    @Test
    @Order(160)
    @Transactional
    void deletePerson_shouldRemoveCorrectly() {
        // arrange
        Person p = new Person("Delete", "Me", LocalDate.of(1950, 1, 1), "X1", false, null);
        personRepository.persist(p);
        Long id = p.getId();

        // act
        var response = personResource.deletePerson(id);
        Log.info(response.getEntity());

        // assert
        assertThat(response.getStatus()).isEqualTo(OK.getStatusCode());
        assertThat(personRepository.findById(id)).isNull();
    }

    @Test
    @Order(170)
    @Transactional
    void Login_shouldSucceed_withCorrectCredentials() {
        // arrange
        String password = org.mindrot.jbcrypt.BCrypt.hashpw("geheim", org.mindrot.jbcrypt.BCrypt.gensalt());
        Person worker = new Person("Max", "Mustermann", LocalDate.of(1980, 3, 20), null, true, password);
        personRepository.persist(worker);

        Person LoginAttempt = new Person("Max", "Mustermann", null, null, true, "geheim");

        // act
        var response = personResource.login(LoginAttempt);
        Log.info(response.getEntity());

        // assert
        assertThat(response.getStatus()).isEqualTo(OK.getStatusCode());
        assertThat(response.getEntity()).isEqualTo("Erfolgreich eingeloggt");
    }

    @Test
    @Order(180)
    @Transactional
    void Login_shouldFail_forSenior() {
        // arrange
        Person senior = new Person("Opa", "Test", LocalDate.of(1945, 6, 30), "Z3", false, null);
        personRepository.persist(senior);

        Person LoginAttempt = new Person("Opa", "Test", null, "Z3", false, "egal");

        // act
        assertThatThrownBy(() -> personResource.login(LoginAttempt))
                .isInstanceOf(NotAuthorizedException.class)
                .extracting(e -> ((NotAuthorizedException) e).getResponse().getStatus())
                .isEqualTo(UNAUTHORIZED.getStatusCode());
    }


}
