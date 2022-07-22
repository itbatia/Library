package com.itbatia.app.services;

import com.itbatia.app.models.Book;
import com.itbatia.app.models.Person;
import com.itbatia.app.repositories.BookRepository;
import com.itbatia.app.repositories.PersonRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(readOnly = true)
public class PersonService {

    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;
    private final BookRepository bookRepository;

    @Autowired
    public PersonService(PersonRepository personRepository,
                         PasswordEncoder passwordEncoder,
                         BookRepository bookRepository) {
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
        this.bookRepository = bookRepository;
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

        updatedPerson.setUsername(personFromDB.getUsername());
        updatedPerson.setPassword(personFromDB.getPassword());
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
        List<Book> personBooks = personRepository.findById(id).get().getBooks();
        if (!personBooks.isEmpty()) {
            personBooks.forEach(book -> {
                book.setTakenAt(null);
                book.setReservedUntil(null);
            });
        }
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

    //Проверка просрочки книги:
    private boolean expiredBook(Book book) {
        long difference = new Date().getTime() - book.getTakenAt().getTime();
        return difference > 864000000; //10 суток в миллисекундах 864000000
    }

    //Поиск пользователей по первой букве(ам) в имени:
    public List<Person> findByFullNameStartingWith(String query) {
        if (!query.isEmpty())
            return personRepository.findByFullNameStartingWith(query);
        return Collections.emptyList();
    }

    //Для отчёта
    public Map<String, Integer> report() {
        List<Person> people = personRepository.findAll();

        if (!people.isEmpty()) {
            Map<String, Integer> report = new LinkedHashMap<>();

            report.put("Всего зарегистрировано пользователей: ", personRepository.findAll().size());
            report.put("Из них взяли книги на руки: ", peopleWithBooksOnHand().size());
            report.put("Забронировали книги на сайте: ", peopleWithReservedBooks().size());
            report.put("Потеряли возможность бронировать книги на сайте: ", peopleWithBookingFalse().size());

            return report;
        } else return Collections.emptyMap();
    }

    public List<Person> getPeopleForReport(int number) {
        if (number == 1) return peopleWithBooksOnHand();
        if (number == 2) return peopleWithReservedBooks();
        if (number == 3) return peopleWithBookingFalse();
        return Collections.emptyList();
    }

    private List<Person> peopleWithBooksOnHand(){
        return booksOnHand().stream().map(Book::getOwner).toList();
    }

    private List<Person> peopleWithReservedBooks(){
        return reservedBooks().stream().map(Book::getOwner).toList();
    }

    private List<Person> peopleWithBookingFalse(){
        return personRepository.findAllByBookingFalse();
    }

    private List<Book> booksOnHand() {
        return bookRepository.findAllByTakenAtNotNull();
    }

    private List<Book> reservedBooks() {
        return bookRepository.findAllByReservedUntilNotNull();
    }
}
