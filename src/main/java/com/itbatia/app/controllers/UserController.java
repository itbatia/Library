package com.itbatia.app.controllers;

import com.itbatia.app.dto.BookDTO;
import com.itbatia.app.dto.PersonToUpdateYourselfDTO;
import com.itbatia.app.models.Book;
import com.itbatia.app.models.Person;
import com.itbatia.app.services.BookService;
import com.itbatia.app.services.PersonService;
import com.itbatia.app.utils.PersonMapper;
import com.itbatia.app.utils.PersonValidator;
import com.itbatia.app.utils.Utility;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
public class UserController {

    private final PersonService personService;
    private final PersonMapper personMapper;
    private final PersonValidator personValidator;
    private final ModelMapper modelMapper;
    private final BookService bookService;
    private final Utility utility;

    @Autowired
    public UserController(PersonService personService, PersonMapper personMapper,
                          PersonValidator personValidator, ModelMapper modelMapper,
                          BookService bookService, Utility utility) {
        this.personService = personService;
        this.personMapper = personMapper;
        this.personValidator = personValidator;
        this.modelMapper = modelMapper;
        this.bookService = bookService;
        this.utility = utility;
    }

    @GetMapping("/show_account")
    public String showAccount() {
        return "user/show_account";
    }

    @GetMapping("/edit_account")
    public String editAccount(Model model) {
        model.addAttribute("person", personMapper.convertToPersonToUpdateYourselfDTO(utility.getUserFromContext()));
        return "user/edit_account";
    }

    @PatchMapping("/edit_account")
    public String updateAccount(@ModelAttribute("person") @Valid PersonToUpdateYourselfDTO personToUpdateYourselfDTO,
                                BindingResult bindingResult) {
        Person personToUpdate = personMapper.convertToPerson(personToUpdateYourselfDTO);

        personValidator.validate(personToUpdate, bindingResult);

        if (bindingResult.hasErrors())
            return "user/edit_account";

        personService.updateAccount(personToUpdate);
        return "redirect:/user/show_account";
    }

    @GetMapping("/my_books")
    public String getUserBooks(Model model) {
        List<Book> userBooks = utility.getUserFromContext().getBooks();

        model.addAttribute("books", userBooks.stream().map(this::convertToBookDTO).collect(Collectors.toList()));
        model.addAttribute("reservedUntilFormatList", utility.getReservedUntilFormatList(userBooks));

        return "user/my_books";
    }

    @PatchMapping("/{id}/remove_reserve")
    public String removeReserve(@PathVariable("id") int id) {
        bookService.removeReserve(id);
        return "redirect:/user/my_books";
    }

    private BookDTO convertToBookDTO(Book book) {
        return modelMapper.map(book, BookDTO.class);
    }
}
