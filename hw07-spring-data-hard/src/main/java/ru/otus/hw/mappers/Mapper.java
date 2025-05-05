package ru.otus.hw.mappers;

public interface Mapper<E, D> {

    D toDto(E object);

    E toObject(D object);
}
