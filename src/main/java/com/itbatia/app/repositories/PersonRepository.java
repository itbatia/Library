package com.itbatia.app.repositories;

import com.itbatia.app.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Integer> {

    Optional<Person> findByUsername(String fullName);

    List<Person> findAllByRole(String role);

    List<Person> findByFullNameStartingWith(String query);
}
