package com.itbatia.app.repositories;

import com.itbatia.app.models.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {

    Optional<Book> findAllByTitleAndAuthor(String title, String author);

    List<Book> findAllByTakenAtNotNull();

    List<Book> findAllByReservedUntilNotNull();

    List<Book> findAllByOwnerNull();
}
