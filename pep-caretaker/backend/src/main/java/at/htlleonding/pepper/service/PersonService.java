package at.htlleonding.pepper.service;

import at.htlleonding.pepper.dto.PersonDto;
import at.htlleonding.pepper.model.Person;
import at.htlleonding.pepper.repository.PersonRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.NotAuthorizedException;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;

@ApplicationScoped
public class PersonService {

    @Inject
    PersonRepository personRepository;

    @Inject
    ImageService imageService;

    public record DeleteResult(long deletedImages) {
    }

    public List<PersonDto> getAll() {
        return personRepository.listAll().stream()
                .map(this::toDto)
                .toList();
    }

    public PersonDto getById(Long id) {
        return toDto(findPerson(id));
    }

    @Transactional
    public Person create(Person person) {
        validateCreate(person);
        hashPasswordIfPlainText(person);
        personRepository.persist(person);
        return person;
    }

    @Transactional
    public Person update(Long id, Person updatedPerson) {
        Person existingPerson = findPerson(id);

        if (updatedPerson.getFirstName() != null) existingPerson.setFirstName(updatedPerson.getFirstName());
        if (updatedPerson.getLastName() != null) existingPerson.setLastName(updatedPerson.getLastName());
        if (updatedPerson.getDob() != null) existingPerson.setDob(updatedPerson.getDob());
        if (updatedPerson.getRoomNo() != null) existingPerson.setRoomNo(updatedPerson.getRoomNo());
        if (updatedPerson.getIsWorker() != null) existingPerson.setIsWorker(updatedPerson.getIsWorker());
        if (updatedPerson.getGender() != null) existingPerson.setGender(updatedPerson.getGender());
        if (updatedPerson.getPassword() != null) {
            existingPerson.setPassword(updatedPerson.getPassword());
            hashPasswordIfPlainText(existingPerson);
        }

        return existingPerson;
    }

    @Transactional
    public DeleteResult delete(Long id) {
        Person person = findPerson(id);
        long deletedImages = imageService.deleteAllForPerson(id);
        personRepository.delete(person);
        return new DeleteResult(deletedImages);
    }

    @Transactional
    public void login(Person loginPerson) {
        if (loginPerson == null || loginPerson.getFirstName() == null || loginPerson.getLastName() == null) {
            throw new NotAuthorizedException("Missing credentials");
        }

        Person person = personRepository.find("firstName = ?1 AND lastName = ?2",
                loginPerson.getFirstName(), loginPerson.getLastName()).firstResult();

        // Einheitliche Meldung fuer jeden Fehlschlag. Vorher unterschied die
        // Antwort zwischen "Benutzer nicht gefunden" und "Falsches Passwort" -
        // damit liess sich ueber diesen unauthentifizierten Endpunkt abfragen,
        // welche Namen im Haus gepflegt werden.
        if (person == null
                || !Boolean.TRUE.equals(person.getIsWorker())
                || loginPerson.getPassword() == null
                || person.getPassword() == null
                || !passwordMatches(loginPerson.getPassword(), person)) {
            throw new NotAuthorizedException("Anmeldung fehlgeschlagen");
        }
    }

    public Person findPerson(Long id) {
        Person person = personRepository.findById(id);
        if (person == null) {
            throw new NotFoundException("Person " + id + " not found");
        }
        return person;
    }

    public PersonDto toDto(Person person) {
        return new PersonDto(
                person.getId(),
                person.getFirstName(),
                person.getLastName(),
                person.getDob(),
                person.getRoomNo(),
                person.getIsWorker(),
                person.getGender()
        );
    }

    private void validateCreate(Person person) {
        if (person == null || person.getFirstName() == null || person.getLastName() == null || person.getRoomNo() == null) {
            throw new BadRequestException("Fehlende Daten: Vorname, Nachname oder Zimmernummer");
        }
    }

    private boolean passwordMatches(String rawPassword, Person person) {
        String storedPassword = person.getPassword();
        if (storedPassword.startsWith("$2a$") || storedPassword.startsWith("$2b$") || storedPassword.startsWith("$2y$")) {
            try {
                return BCrypt.checkpw(rawPassword, storedPassword);
            } catch (IllegalArgumentException e) {
                return false;
            }
        }

        boolean matches = rawPassword.equals(storedPassword);
        if (matches) {
            person.setPassword(BCrypt.hashpw(rawPassword, BCrypt.gensalt()));
        }
        return matches;
    }

    private void hashPasswordIfPlainText(Person person) {
        String password = person.getPassword();
        if (password == null || password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$")) {
            return;
        }
        person.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
    }
}
