package com.itbatia.app.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "Person")
public class Person {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty(message = "Введите логин")
    @Size(min = 3, max = 50, message = "Логин должен быть от 3 до 50 символов длиной")
    @NotBlank(message = "Логин не может состоять только из пробелов")
    @Column(name = "username")
    private String username;

    @NotEmpty(message = "Введите пароль")
    @Size(min = 4, message = "Длинна пароля не должна быть ниже 4 символов")
    @Column(name = "password")
    private String password;

    @NotEmpty(message = "Введите ФИО")
    @Size(min = 8, max = 100, message = "Логин должен быть от 8 до 100 символов длиной")
    @NotBlank(message = "ФИО не может состоять только из пробелов")
    @Column(name = "full_name")
    private String fullName;

    @NotNull(message = "Введите год рождения")
    @Min(value = 1900, message = "Год рождения не может быть ниже 1900")
    @Max(value = 2200, message = "Год рождения не может быть выше 2200")
    @Column(name = "year_of_birth")
    private Integer yearOfBirth;

    @Column(name = "role")
    private String role;

    @Column(name = "booking")
    private Boolean booking;

    @Column(name = "created_at")
    @Temporal(TemporalType.DATE)
    private Date createdAt;

    @OneToMany (mappedBy = "owner")
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    private List<Book> books;
}
