package com.itbatia.app.dto;

import com.itbatia.app.models.Genre;
import com.itbatia.app.models.Person;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
public class BookDTO {

    private Integer id;

    @NotEmpty(message = "Введите название книги")
    @Size(min = 2, max = 100, message = "Название книги должно быть от 2 до 100 символов")
    @NotBlank(message = "Название не может состоять только из пробелов")
    private String title;

    @NotEmpty(message = "Укажите автора")
    @Size(min = 2, max = 100, message = "Имя автора должно быть от 2 до 100 символов")
    @NotBlank(message = "Название не может состоять только из пробелов")
    private String author;

    private Genre genre;

    private Person owner;

    private LocalDateTime reservedUntil;

    private Date takenAt;

    private boolean timeExpired;
}
