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
import java.time.ZoneId;
import java.util.*;

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
        return bookRepository.findAllByTitleAndAuthor(book.getTitle(), book.getAuthor());
    }

    public List<Book> getByReservedUntilNotNull() {
        return bookRepository.findAllByReservedUntilNotNull();
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
            book.setReservedUntil(LocalDateTime.now(ZoneId.of("Europe/Minsk")).plusDays(1));
        });
    }
    // Heroku использует GMT (Среднее Время По Гринвичу). GMT + 3ч = время в Минске,
    // поэтому внёс корректировку часового пояса.


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
        if (!query.isEmpty()) {
            List<String> wordsOfQuery = queryEditing(query);
            if (wordsOfQuery.size()==1) {
                return bookRepository.findAllByTitleStartingWith(wordsOfQuery.get(0));
            } else {
                List<Book> resultList = bookRepository.findAllByTitleStartingWith(wordsOfQuery.get(0));
                resultList.addAll(bookRepository.findAllByTitleStartingWith(wordsOfQuery.get(1)));
                return resultList.stream().distinct().toList();
            }
        }
        return Collections.emptyList();
    }

    //Поиск книги по первой букве(ам) имени автора:
    public List<Book> findByAuthorStartingWith(String query) {
        if (!query.isEmpty()) {
            List<String> wordsOfQuery = queryEditing(query);
            if (wordsOfQuery.size()==1) {
                return bookRepository.findAllByAuthorStartingWith(wordsOfQuery.get(0));
            } else {
                List<Book> resultList = bookRepository.findAllByAuthorStartingWith(wordsOfQuery.get(0));
                resultList.addAll(bookRepository.findAllByAuthorStartingWith(wordsOfQuery.get(1)));
                return resultList.stream().distinct().toList();
            }
        }
        return Collections.emptyList();
    }

    private List<String> queryEditing(String userQuery) {
        List<String> wordsOfQuery = new ArrayList<>();
        String query = userQuery.strip();

        if (query.contains(" ")) {
            String[] array = query.split(" ");
            wordsOfQuery.add(queryEditingInternal(array[0]));
            wordsOfQuery.add(queryEditingInternal(array[1]));
        } else {
            wordsOfQuery.add(queryEditingInternal(query));
        }
        return wordsOfQuery;
    }

    private String queryEditingInternal(String query) {
        return query.substring(0, 1).toUpperCase().concat(query.substring(1));
    }

    //Для отчёта
    public Map<String, Integer> report() {
        List<Book> books = bookRepository.findAll();

        if (!books.isEmpty()) {
            Map<String, Integer> report = new LinkedHashMap<>();

            int booksInHand = 0;
            int booksReserved = 0;
            int booksFreely = 0;

            for (Book book : books) {
                if (book.getOwner() == null) {
                    booksFreely++;
                } else if (book.getTakenAt() != null) {
                    booksInHand++;
                } else booksReserved++;
            }

            report.put("Всего книг в библиотеке: ", books.size());
            report.put("Из них на руках: ", booksInHand);
            report.put("Забронировано: ", booksReserved);
            report.put("Свободно: ", booksFreely);
            return report;

        } else return Collections.emptyMap();
    }

    public List<Book> partBooksForReport(int number) {
        if (number == 1) return bookRepository.findAllByOwnerNull();
        if (number == 2) return bookRepository.findAllByTakenAtNotNull();
        if (number == 3) return bookRepository.findAllByReservedUntilNotNull();
        return Collections.emptyList();
    }

    public List<Book> freeBooks(){
        return bookRepository.findAllByOwnerNull();
    }
}

