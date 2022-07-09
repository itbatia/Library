package com.itbatia.app.utils;

import com.itbatia.app.models.Book;
import com.itbatia.app.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Objects;

@Component
public class BookValidator implements Validator {

    private final BookService bookService;

    @Autowired
    public BookValidator(BookService bookService) {
        this.bookService = bookService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Book.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Book bookToCheck = (Book) target;

        if ((bookToCheck.getId() == null && bookService.findByTitleAndAuthor(bookToCheck).isPresent()) ||
                ((bookToCheck.getId() != null) && bookService.findByTitleAndAuthor(bookToCheck).isPresent() &&
                        !Objects.equals(bookService.findByTitleAndAuthor(bookToCheck).get().getId(), bookToCheck.getId()))) {
            errors.rejectValue("title", "", "Книга с таким названием и автором уже существует!");
        }
    }
}