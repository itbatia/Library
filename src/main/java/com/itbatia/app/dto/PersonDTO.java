package com.itbatia.app.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.util.Date;

@Getter
@Setter
public class PersonDTO {

    private Integer id;

    @NotEmpty(message = "Введите ФИО")
    @Size(min = 8, max = 100, message = "Логин должен быть от 8 до 100 символов длиной")
    private String fullName;

    @NotNull(message = "Введите год рождения")
    @Min(value = 1900, message = "Год рождения не может быть ниже 1900")
    @Max(value = 2200, message = "Год рождения не может быть выше 2200")
    private Integer yearOfBirth;

    private Boolean booking;

    private Date createdAt;
}
