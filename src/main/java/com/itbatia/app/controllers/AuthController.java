package com.itbatia.app.controllers;

import com.itbatia.app.dto.PersonToRegisterDTO;
import com.itbatia.app.models.Person;
import com.itbatia.app.services.PersonService;
import com.itbatia.app.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final PersonService personService;
    private final PersonValidator personValidator;
    private final PersonMapper personMapper;

    @Autowired
    public AuthController(PersonService personService, PersonValidator personValidator, PersonMapper personMapper) {
        this.personService = personService;
        this.personValidator = personValidator;
        this.personMapper = personMapper;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/registration")
    public String registrationPage(@ModelAttribute("person") PersonToRegisterDTO personToRegisterDTO) {
        return "auth/registration";
    }

    @PostMapping("/registration")
    public String performRegistration(@ModelAttribute("person") @Valid PersonToRegisterDTO personToRegisterDTO,
                                      BindingResult bindingResult) {

        Person person = personMapper.convertToPerson(personToRegisterDTO);

        personValidator.validate(person, bindingResult);

        if (bindingResult.hasErrors()) {
            return "auth/registration";
        }
        personService.register(person);
        return "redirect:/auth/login";
    }
}