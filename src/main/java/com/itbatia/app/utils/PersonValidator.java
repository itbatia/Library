package com.itbatia.app.utils;

import com.itbatia.app.models.Person;
import com.itbatia.app.services.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Objects;

@Component
public class PersonValidator implements Validator {

    private final PersonService personService;

    @Autowired
    public PersonValidator(PersonService personService) {
        this.personService = personService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Person.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Person person = (Person) target;

        // Для регистрации нового пользователя
        if (person.getId() == null && personService.findByUsername(person.getUsername()).isPresent()) {
            errors.rejectValue("username", "", "Человек с таким логином уже зарегистрирован!");
            return;
        }
        // Для изменения данных существующего пользователя
        if (!(person.getId() == null) && personService.findByUsername(person.getUsername()).isPresent()) {
            if (!Objects.equals(
                    personService.findByUsername(person.getUsername()).get().getId(),
                    person.getId())) {
                errors.rejectValue("username", "", "Человек с таким логином уже зарегистрирован!");
            }
        }
    }
}
