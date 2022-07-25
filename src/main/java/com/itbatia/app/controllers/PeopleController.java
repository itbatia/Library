package com.itbatia.app.controllers;

import com.itbatia.app.dto.BookDTO;
import com.itbatia.app.dto.PersonToUpdateDTO;
import com.itbatia.app.models.Book;
import com.itbatia.app.models.Person;
import com.itbatia.app.services.PersonService;
import com.itbatia.app.utils.PersonMapper;
import com.itbatia.app.utils.Utility;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/people")
public class PeopleController {

    private final PersonService personService;
    private final PersonMapper personMapper;
    private final ModelMapper modelMapper;
    private final Utility utility;

    @Autowired
    public PeopleController(PersonService personService, PersonMapper personMapper,
                            ModelMapper modelMapper, Utility utility) {
        this.personService = personService;
        this.personMapper = personMapper;
        this.modelMapper = modelMapper;
        this.utility = utility;
    }

    @GetMapping
    public String getAllPeopleByRoleUser(Model model) {
        model.addAttribute("people", personService.findAllByRole("ROLE_USER")
                .stream().map(personMapper::convertToPersonDTO).toList());

        return "people/all_people";
    }

    @GetMapping("/{id}")
    public String getById(@PathVariable("id") int id, Model model) {
        List<Book> personBooks = personService.getBooksByPersonId(id);

        model.addAttribute("person", personMapper.convertToPersonDTO(personService.findById(id)));
        model.addAttribute("books", personBooks.stream().map(this::convertToBookDTO).toList());
        model.addAttribute("reserveTime", utility.getReservedUntilFormatList(personBooks));
        return "people/show";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable("id") int id, Model model) {
        model.addAttribute("person", personMapper.convertToPersonToUpdateDTO(personService.findById(id)));
        return "people/edit";
    }

    @PatchMapping("/{id}")
    public String update(@PathVariable("id") int id,
                         @ModelAttribute("person") @Valid PersonToUpdateDTO personDTO, BindingResult bindingResult) {
        Person personToUpdate = personMapper.convertToPerson(personDTO);

        if (bindingResult.hasErrors())
            return "people/edit";

        personService.updatePerson(id, personToUpdate);
        return "redirect:/people/" + id;
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id) {
        personService.delete(id);
        return "redirect:/people";
    }

    @GetMapping("/search")
    public String searchPage() {
        return "people/search";
    }

    @PostMapping("/search")
    public String makeSearch(@RequestParam("query") String query, Model model) {

        model.addAttribute("people", personService.findByFullNameContainsQuery(query)
                .stream().map(personMapper::convertToPersonDTO).toList());

        return "people/search";
    }

    @GetMapping("/report")
    public String report(Model model) {
        model.addAttribute("report", personService.report());
        return "people/report";
    }

    @GetMapping("/report/{number}")
    public String getPeopleForReport(@PathVariable("number") Integer number, Model model) {
        model.addAttribute("people", personService.getPeopleForReport(number)
                .stream().map(personMapper::convertToPersonDTO).toList());
        model.addAttribute("number", number);
        return "people/people_for_report";
    }

    private BookDTO convertToBookDTO(Book book) {
        return modelMapper.map(book, BookDTO.class);
    }
}
