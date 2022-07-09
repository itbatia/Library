package com.itbatia.app.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.util.Date;

@Getter
@Setter
public class PersonToUpdateDTO {

    private Integer id;

    @NotEmpty(message = "Введите логин")
    @Size(min = 3, max = 50, message = "Логин должен быть от 3 до 50 символов длиной")
    @NotBlank(message = "Логин не может состоять только из пробелов")
    private String username;

    @NotEmpty(message = "Введите пароль")
    @Size(min = 4, message = "Длинна пароля не должна быть ниже 4 символов")
    private String password;

    @NotNull(message = "Введите год рождения")
    @Min(value = 1900, message = "Год рождения не может быть ниже 1900")
    @Max(value = 2200, message = "Год рождения не может быть выше 2200")
    private Integer yearOfBirth;

    @NotEmpty(message = "Введите имя")
    @Size(min = 2, max = 20, message = "Имя должно быть от 2 до 20 символов длиной")
    @NotBlank(message = "Логин не может состоять только из пробелов")
    private String firstName;

    @NotEmpty(message = "Введите фамилию")
    @Size(min = 2, max = 20, message = "Фамилия должна быть от 2 до 20 символов длиной")
    @NotBlank(message = "Логин не может состоять только из пробелов")
    private String lastName;

    @NotEmpty(message = "Введите отчество")
    @Size(min = 2, max = 20, message = "Отчество должно быть от 2 до 20 символов длиной")
    @NotBlank(message = "Логин не может состоять только из пробелов")
    private String patronymic;

    private String role;

    private Boolean booking;

    private Date createdAt;
}
