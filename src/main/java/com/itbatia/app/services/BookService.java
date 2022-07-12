package com.itbatia.app.services;

import com.itbatia.app.models.Book;
import com.itbatia.app.models.Person;
import com.itbatia.app.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Book findById(int id) {
        return bookRepository.findById(id).orElse(null);
    }

    public List<Book> getAllBooks(String sorter, Integer page, Integer size) {
        if (sorter == null) {
            return bookRepository.findAll();
        } else if (!sorter.equals("no") && (page == null || size == null || page < 0 || size < 1)) {
            //Только сортировка книг:
            return bookRepository.findAll(Sort.by(sorter));
        } else if (sorter.equals("no") && (page != null && size != null && page >= 0 && size > 0)) {
            //Только пагинация книг:
            return bookRepository.findAll(PageRequest.of(page - 1, size)).getContent();
        } else if (!sorter.equals("no")) {
            //Пагинация + сортировка:
            return bookRepository.findAll(PageRequest.of(page - 1, size, Sort.by(sorter))).getContent();
        } else return bookRepository.findAll();
    }

    //Для валидатора
    public Optional<Book> findByTitleAndAuthor(Book book) {
        return bookRepository.findByTitleAndAuthor(book.getTitle(), book.getAuthor());
    }

    public List<Book> getByReservedUntilNotNull() {
        return bookRepository.findByReservedUntilNotNull();
    }

    @Transactional
    public void updateReserveUntilAndOwner(int bookId) {
        Book bookToUpdate = bookRepository.findById(bookId).get();
        bookToUpdate.setReservedUntil(null);
        bookToUpdate.setOwner(null);
    }

    @Transactional
    @PreAuthorize("hasRole('ROLE_LIBRARIAN') or hasRole('ROLE_ADMIN')")
    public void save(Book book) {
        bookRepository.save(book);
    }

    @Transactional
    @PreAuthorize("hasRole('ROLE_LIBRARIAN') or hasRole('ROLE_ADMIN')")
    public void update(Book updatedBook) {
        Book toBeUpdatedBook = bookRepository.findById(updatedBook.getId()).get();

        //Чтобы не терялась связь при обновлении:
        updatedBook.setOwner(toBeUpdatedBook.getOwner());
        updatedBook.setTakenAt(toBeUpdatedBook.getTakenAt());
        updatedBook.setReservedUntil(toBeUpdatedBook.getReservedUntil());

        //Книга не находилась в Persistent context, поэтому нежен save():
        bookRepository.save(updatedBook);
    }

    @Transactional
    @PreAuthorize("hasRole('ROLE_LIBRARIAN') or hasRole('ROLE_ADMIN')")
    public void delete(int id) {
        bookRepository.deleteById(id);
    }

    //Получаем человека, которому принадлежит книга с указанным id
    public Person getBookOwner(int id) {
        return bookRepository.findById(id).map(Book::getOwner).orElse(null);
    }

    // Назначает книгу человеку (этот метод вызывается, когда человек забирает книгу из библиотеки)
    @Transactional
    public void assign(int id, Person selectedPerson) {
        bookRepository.findById(id).ifPresent(book -> {
            book.setOwner(selectedPerson);
            book.setTakenAt(new Date());
            book.setReservedUntil(null);
        });
    }

    // Освобождает книгу (этот метод вызывается, когда человек возвращает книгу в библиотеку)
    @Transactional
    public void release(int id) {
        bookRepository.findById(id).ifPresent(book -> {
            book.setOwner(null);
            book.setTakenAt(null);
        });
    }

    // Этот метод вызывается, когда человек бронирует книгу на сайте
    @Transactional
    public void makeReservation(int id, Person selectedPerson) {
        bookRepository.findById(id).ifPresent(book -> {
            book.setOwner(selectedPerson);
            book.setReservedUntil(LocalDateTime.now().plusDays(1));
        });
    }

    // Этот метод вызывается, когда пользователь или библиотекарь снимает бронь с книги
    @Transactional
    public void removeReserve(int id) {
        bookRepository.findById(id).ifPresent(book -> {
            book.setOwner(null);
            book.setReservedUntil(null);
        });
    }

    //Поиск книги по первой букве(ам) в названии:
    public List<Book> findByTitleStartingWith(String query) {
        if (!query.isBlank())
            return bookRepository.findByTitleStartingWith(query);
        return Collections.emptyList();
    }

    //Поиск книги по первой букве(ам) имени автора:
    public List<Book> findByAuthorStartingWith(String query) {
        if (!query.isBlank())
            return bookRepository.findByAuthorStartingWith(query);
        return Collections.emptyList();
    }
}

