package com.itbatia.app.utils;

import com.itbatia.app.dto.PersonDTO;
import com.itbatia.app.dto.PersonToUpdateDTO;
import com.itbatia.app.dto.PersonToUpdateYourselfDTO;
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

    public PersonToUpdateYourselfDTO convertToPersonToUpdateYourselfDTO(Person person) {
        PersonToUpdateYourselfDTO personDTO = new PersonToUpdateYourselfDTO();

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

    public PersonToUpdateDTO convertToPersonToUpdateDTO(Person person) {
        PersonToUpdateDTO personDTO = new PersonToUpdateDTO();

        personDTO.setId(person.getId());
        personDTO.setYearOfBirth(person.getYearOfBirth());
        personDTO.setBooking(person.getBooking());
        personDTO.setRole(person.getRole());

        String[] fullName = person.getFullName().split(" ");
        personDTO.setLastName(fullName[0]);
        personDTO.setFirstName(fullName[1]);
        personDTO.setPatronymic(fullName[2]);

        return personDTO;
    }

    public Person convertToPerson (PersonToUpdateYourselfDTO personToUpdateYourselfDTO){
        Person person = new Person();

        String fullName = personToUpdateYourselfDTO.getLastName() + " " +
                personToUpdateYourselfDTO.getFirstName() + " " +
                personToUpdateYourselfDTO.getPatronymic();

        person.setId(personToUpdateYourselfDTO.getId());
        person.setUsername(personToUpdateYourselfDTO.getUsername());
        person.setPassword(personToUpdateYourselfDTO.getPassword());
        person.setFullName(fullName);
        person.setYearOfBirth(personToUpdateYourselfDTO.getYearOfBirth());

        return person;
    }

    public Person convertToPerson (PersonToUpdateDTO personToUpdateDTO){
        Person person = new Person();

        String fullName = personToUpdateDTO.getLastName() + " " +
                personToUpdateDTO.getFirstName() + " " +
                personToUpdateDTO.getPatronymic();

        person.setId(personToUpdateDTO.getId());
        person.setFullName(fullName);
        person.setYearOfBirth(personToUpdateDTO.getYearOfBirth());
        person.setBooking(personToUpdateDTO.getBooking());
        person.setRole("ROLE_" + personToUpdateDTO.getRole());

        return person;
    }

    public Person convertToPerson(PersonDTO personDTO) {
        return modelMapper.map(personDTO, Person.class);
    }

    public PersonDTO convertToPersonDTO(Person person) {
        return modelMapper.map(person, PersonDTO.class);
    }
}
