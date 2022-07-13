package com.itbatia.app.controllers;

import com.itbatia.app.dto.BookDTO;
import com.itbatia.app.dto.PersonDTO;
import com.itbatia.app.models.Book;
import com.itbatia.app.models.Person;
import com.itbatia.app.services.BookService;
import com.itbatia.app.services.PersonService;
import com.itbatia.app.utils.BookValidator;
import com.itbatia.app.utils.PersonMapper;
import com.itbatia.app.utils.Utility;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;
    private final ModelMapper modelMapper;
    private final BookValidator bookValidator;
    private final PersonService personService;
    private final PersonMapper personMapper;
    private final Utility utility;

    @Autowired
    public BookController(BookService bookService, ModelMapper modelMapper,
                          BookValidator bookValidator, PersonService personService,
                          PersonMapper personMapper, Utility utility) {
        this.bookService = bookService;
        this.modelMapper = modelMapper;
        this.bookValidator = bookValidator;
        this.personService = personService;
        this.personMapper = personMapper;
        this.utility = utility;
    }

    @GetMapping()
    public String getAllBooks(@RequestParam(value = "sorter", required = false) String sorter,
                              @RequestParam(value = "page", required = false) Integer page,
                              @RequestParam(value = "size", required = false) Integer size,
                              Model model) {
        model.addAttribute("books", bookService.getAllBooks(sorter, page, size)
                .stream().map(this::convertToBookDTO).collect(Collectors.toList()));
        return "books/allBooks";
    }

    @GetMapping("/{id}")
    public String getBookById(@PathVariable("id") int id, Model model,
                              @ModelAttribute("person") PersonDTO personDTO) {
        Book bookById = bookService.findById(id);
        Person bookOwner = bookService.getBookOwner(id);
        Person currentUser = utility.getUserFromContext();

        model.addAttribute("book", convertToBookDTO(bookById));
        model.addAttribute("currentUser", currentUser);

        if (bookById.getReservedUntil() != null) {
            model.addAttribute("reserveTime", utility.timeFormat(bookById.getReservedUntil()));
        }
        if (bookOwner != null) {
            model.addAttribute("owner", personMapper.convertToPersonDTO(bookOwner));
            if (bookOwner.getId().equals(currentUser.getId())) {
                model.addAttribute("currentUser", currentUser);
            } else {
                model.addAttribute("currentUser", null);
            }
        } else {
            model.addAttribute("people", personService.findAllByRole("ROLE_USER")
                    .stream().map(personMapper::convertToPersonDTO).collect(Collectors.toList()));
        }
        return "books/show";
    }

    @GetMapping("/new")
    public String newBook(@ModelAttribute("book") BookDTO bookDTO) {
        return "books/new";
    }

    @PostMapping("/new")
    public String create(@ModelAttribute("book") @Valid BookDTO bookDTO, BindingResult bindingResult) {
        Book newBook = convertToBook(bookDTO);

        bookValidator.validate(newBook, bindingResult);

        if (bindingResult.hasErrors())
            return "books/new";

        bookService.save(newBook);
        return "redirect:/books";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable("id") int id, Model model) {
        model.addAttribute("book", convertToBookDTO(bookService.findById(id)));
        return "books/edit";
    }

    @PatchMapping("/{id}")
    public String update(@PathVariable("id") int id, @ModelAttribute("book") @Valid BookDTO bookDTO,
                         BindingResult bindingResult) {
        Book updatedBook = convertToBook(bookDTO);

        bookValidator.validate(updatedBook, bindingResult);

        if (bindingResult.hasErrors())
            return "books/edit";

        bookService.update(updatedBook);

        return "redirect:/books";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id) {
        bookService.delete(id);
        return "redirect:/books";
    }

    @PatchMapping("/{id}/assign")
    public String assign(@PathVariable("id") int id, @ModelAttribute("person") PersonDTO selectPersonDTO) {
        bookService.assign(id, personMapper.convertToPerson(selectPersonDTO));
        return "redirect:/books/" + id;
    }

    @PatchMapping("/{id}/release")
    public String release(@PathVariable("id") int id) {
        bookService.release(id);
        return "redirect:/books/" + id;
    }

    @PatchMapping("/{id}/reservation")
    public String makeReservation(@PathVariable("id") int id) {
        bookService.makeReservation(id, utility.getUserFromContext());
        return "redirect:/books/" + id;
    }

    @PatchMapping("/{id}/removeReserve")
    public String removeReserve(@PathVariable("id") int id) {
        bookService.removeReserve(id);
        return "redirect:/books/" + id;
    }

    @GetMapping("/search")
    public String searchPage() {
        return "books/search";
    }

    @PostMapping("/search")
    public String makeSearch(@RequestParam("query") String query, Model model,
                             @RequestParam("searchBy") String searchBy) {
        List<Book> books = new ArrayList<>();
        if (searchBy.equals("title")) {
            books = bookService.findByTitleStartingWith(query);
        } else if (searchBy.equals("author")) {
            books = bookService.findByAuthorStartingWith(query);
        }
        model.addAttribute("books", books.stream().map(this::convertToBookDTO).collect(Collectors.toList()));
        model.addAttribute("amount", books.size());
        model.addAttribute("currentUser", utility.getUserFromContext());

        return "books/search";
    }

    private Book convertToBook(BookDTO bookDTO) {
        return modelMapper.map(bookDTO, Book.class);
    }

    private BookDTO convertToBookDTO(Book book) {
        return modelMapper.map(book, BookDTO.class);
    }
}
