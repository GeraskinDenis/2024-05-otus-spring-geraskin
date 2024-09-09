package ru.otus.hw.exceptions;

public class EntityNotSavedException extends RuntimeException {

    public EntityNotSavedException(String message) {
        super(message);
    }
}
