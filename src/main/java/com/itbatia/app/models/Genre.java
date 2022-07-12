package com.itbatia.app.models;

public enum Genre {
    HISTORY("История"),
    THRILLER("Триллер"),
    DETECTIVE("Детектив"),
    CLASSIC("Классика"),
    SCIENCE("Наука"),
    NOVEL("Роман"),
    SATIRE("Комедия"),
    HORROR("Ужасы"),
    RELIGIOUS("Религия"),
    ACTION("Боевик"),
    HEALTH("Здоровье"),
    ART("Искусство"),
    BUSINESS("Бизнес"),
    CHILDREN("Сказка"),
    DICTIONARY("Словарь"),
    ENCYCLOPEDIA("Энциклопедия"),
    ANTHOLOGY("Антология"),
    FANTASY("Фэнтези"),
    PSYCHOLOGY("Психология"),
    MYSTIC("Мистика"),
    PHILOSOPHY("Философия"),
    SAGA("Сага");

    private final String genreTitle;

    Genre(String genreTitle) {
        this.genreTitle = genreTitle;
    }

    public String getGenreTitle() {
        return genreTitle;
    }
}
