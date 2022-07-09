package com.itbatia.app.utils;

import com.itbatia.app.dto.PersonDTO;
import com.itbatia.app.dto.PersonToUpdateDTO;
import com.itbatia.app.dto.PersonToRegisterDTO;
import com.itbatia.app.models.Person;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PersonMapper {

    private final ModelMapper modelMapper;

    @Autowired
    public PersonMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Person convertToPerson(PersonToRegisterDTO personToRegisterDTO) {
        Person person = new Person();

        String fullName = personToRegisterDTO.getLastName() + " " +
                personToRegisterDTO.getFirstName() + " " +
                personToRegisterDTO.getPatronymic();

        person.setUsername(personToRegisterDTO.getUsername());
        person.setPassword(personToRegisterDTO.getPassword());
        person.setYearOfBirth(personToRegisterDTO.getYearOfBirth());
        person.setFullName(fullName);
        person.setRole("ROLE_" + personToRegisterDTO.getRole());

        return person;
    }

    public PersonToUpdateDTO convertToPersonToUpdateDTO(Person person) {
        PersonToUpdateDTO personDTO = new PersonToUpdateDTO();

        personDTO.setId(person.getId());
        personDTO.setUsername(person.getUsername());
        personDTO.setPassword("");
        personDTO.setYearOfBirth(person.getYearOfBirth());

        String[] fullName = person.getFullName().split(" ");
        personDTO.setLastName(fullName[0]);
        personDTO.setFirstName(fullName[1]);
        personDTO.setPatronymic(fullName[2]);

        return personDTO;
    }

    public Person convertToPerson (PersonToUpdateDTO personToUpdateDTO){
        Person person = new Person();

        String fullName = personToUpdateDTO.getLastName() + " " +
                personToUpdateDTO.getFirstName() + " " +
                personToUpdateDTO.getPatronymic();

        person.setId(personToUpdateDTO.getId());
        person.setUsername(personToUpdateDTO.getUsername());
        person.setPassword(personToUpdateDTO.getPassword());
        person.setYearOfBirth(personToUpdateDTO.getYearOfBirth());
        person.setFullName(fullName);

        return person;
    }

    public Person convertToPerson(PersonDTO personDTO) {
        return modelMapper.map(personDTO, Person.class);
    }

    public PersonDTO convertToPersonDTO(Person person) {
        return modelMapper.map(person, PersonDTO.class);
    }
}
