package com.itbatia.app.utils;

import com.itbatia.app.models.Book;
import com.itbatia.app.models.Person;
import com.itbatia.app.security.PersonDetails;
import com.itbatia.app.services.BookService;
import com.itbatia.app.services.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class Utility {

    private final PersonService personService;
    private final BookService bookService;

    @Autowired
    public Utility(PersonService personService, BookService bookService) {
        this.personService = personService;
        this.bookService = bookService;
    }

    public Person getUserFromContext() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) auth.getPrincipal();
        return personService.findById(personDetails.getPerson().getId());
    }

    public Map<Integer, String> getReservedUntilFormatList(List<Book> userBooks) {

        Map<Integer, String> reservedUntilFormatList = new HashMap<>();

        userBooks.forEach(book -> {
            if (book.getReservedUntil() != null)
                reservedUntilFormatList.put(book.getId(), timeFormat(book.getReservedUntil()));
        });
        return reservedUntilFormatList;
    }

    public String timeFormat(LocalDateTime dateOfReserve) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM HH:mm");
        return dateOfReserve.format(formatter);
    }

    @PostConstruct
    public void updateBooking() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    bookService.getByReservedUntilNotNull().forEach(book -> {
                        if (book.getReservedUntil().isBefore(LocalDateTime.now())) {
                            personService.updateBookingForPerson(book.getOwner().getId());
                            bookService.updateReserveUntilAndOwner(book.getId());
                        }
                    });
                    try {
                        Thread.sleep(3600000); //1 час = 3600000 мс
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();
    }
}
