package com.itbatia.app.services;

import com.itbatia.app.models.Book;
import com.itbatia.app.models.Person;
import com.itbatia.app.repositories.PersonRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PersonService {

    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PersonService(PersonRepository personRepository, PasswordEncoder passwordEncoder) {
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Person> findAllByRole(String role) {
        return personRepository.findAllByRole(role);
    }

    @Transactional
    public void register(Person person) {
        person.setPassword(passwordEncoder.encode(person.getPassword()));
        person.setBooking(true);
        person.setCreatedAt(new Date());
        personRepository.save(person);
    }

    public Optional<Person> findByUsername(String username) {
        return personRepository.findByUsername(username);
    }

    public Person findById(int id) {
        return personRepository.findById(id).orElse(null);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public void updatePerson(int id, Person updatedPerson) {
        enrichPerson(id, updatedPerson);
        personRepository.save(updatedPerson);
    }

    private void enrichPerson(int id, Person updatedPerson) {
        Person personFromDB = findById(id);

        updatedPerson.setId(id);
        updatedPerson.setUsername(personFromDB.getUsername());
        updatedPerson.setPassword(personFromDB.getPassword());
        updatedPerson.setRole(personFromDB.getRole());
        updatedPerson.setCreatedAt(personFromDB.getCreatedAt());
    }

    @Transactional
    public void updateAccount(Person updatedPerson) {
        enrichPersonForAccount(updatedPerson);
        personRepository.save(updatedPerson);
    }

    private void enrichPersonForAccount(Person updatedPerson) {
        Person personFromDB = findById(updatedPerson.getId());

        updatedPerson.setPassword(passwordEncoder.encode(updatedPerson.getPassword()));
        updatedPerson.setRole(personFromDB.getRole());
        updatedPerson.setBooking(personFromDB.getBooking());
        updatedPerson.setCreatedAt(personFromDB.getCreatedAt());
    }

    // Метод вызывается если пользователь просрочил бронь книги
    @Transactional
    public void updateBookingForPerson(int personId) {
        personRepository.findById(personId).get().setBooking(false);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public void delete(int id) {
//        Person personToDelete = personRepository.findById(id).get();
//        List<Book> personBooks = personToDelete.getBooks();
//        if(!personBooks.isEmpty()){
//            personBooks.forEach((book -> {
//                book.setTakenAt(null);
//                book.setReservedUntil(null);
//            }));
//        }
        List<Book> personBooks = personRepository.findById(id).get().getBooks();
        if (!personBooks.isEmpty()) {
            personBooks.forEach(book -> {
                book.setTakenAt(null);
                book.setReservedUntil(null);
            });
        }
//        List<Book> personBooks = getBooksByPersonId(id);
//        Hibernate.initialize(personBooks);
//        personBooks.forEach(book -> {
//            book.setTakenAt(null);
//            book.setReservedUntil(null);
//        });

        personRepository.deleteById(id);
    }

    public List<Book> getBooksByPersonId(int id) {

        Optional<Person> person = personRepository.findById(id);

        if (person.isPresent()) {
            //На случай, если код поменяется и удалится итерация по книгам:
            Hibernate.initialize(person.get().getBooks());

            person.get().getBooks().forEach(book -> {
                if (book.getTakenAt() != null)
                    book.setTimeExpired(expiredBook(book));
            });
            return person.get().getBooks();
        } else {
            return Collections.emptyList();
        }
    }

    //Проверка просрочки книги?
    private boolean expiredBook(Book book) {
        long difference = new Date().getTime() - book.getTakenAt().getTime();
        return difference > 864000000; //10 суток в миллисекундах 864000000
    }
}
