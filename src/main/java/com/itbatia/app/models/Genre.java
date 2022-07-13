package com.itbatia.app.models;

public enum Genre {
    ANTHOLOGY("Антология"),
    BUSINESS("Бизнес"),
    ACTION("Боевик"),
    DETECTIVE("Детектив"),
    HEALTH("Здоровье"),
    ART("Искусство"),
    HISTORY("История"),
    CLASSIC("Классика"),
    SATIRE("Комедия"),
    MYSTIC("Мистика"),
    SCIENCE("Наука"),
    PSYCHOLOGY("Психология"),
    NOVEL("Роман"),
    RELIGIOUS("Религия"),
    SAGA("Сага"),
    CHILDREN("Сказка"),
    DICTIONARY("Словарь"),
    THRILLER("Триллер"),
    HORROR("Ужасы"),
    FANTASY("Фэнтези"),
    PHILOSOPHY("Философия"),
    ENCYCLOPEDIA("Энциклопедия");

    private final String genreTitle;

    Genre(String genreTitle) {
        this.genreTitle = genreTitle;
    }

    public String getGenreTitle() {
        return genreTitle;
    }
}
