package com.itbatia.app.models;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Entity
@Table(name = "Book")
public class Book {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty(message = "Введите название книги")
    @Size(min = 2, max = 100, message = "Название книги должно быть от 2 до 100 символов")
    @NotBlank(message = "Название не может состоять только из пробелов")
    @Column(name = "title")
    private String title;

    @NotEmpty(message = "Укажите автора")
    @Size(min = 2, max = 100, message = "Имя автора должно быть от 2 до 100 символов")
    @NotBlank(message = "Название не может состоять только из пробелов")
    @Column(name = "author")
    private String author;

    @Column(name = "genre")
    @Enumerated(EnumType.STRING)
    private Genre genre;

    @ManyToOne
    @JoinColumn(name = "person_id", referencedColumnName = "id")
    private Person owner;

    @Column(name = "reserved_until")
    private LocalDateTime reservedUntil;

    @Column(name = "taken_at")
    @Temporal(TemporalType.DATE)
    private Date takenAt;

    @Transient
    private boolean timeExpired;
}
