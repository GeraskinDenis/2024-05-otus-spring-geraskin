package ru.otus.hw.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Book {

    @Id
    @EqualsAndHashCode.Exclude
    private String id;

    @Indexed(unique = true)
    private String uuid;

    private String title;

    private Author author;

    @DBRef
    private List<Genre> genres;

    public Book(String uuid, String title, Author author, List<Genre> genres) {
        this.uuid = uuid;
        this.title = title;
        this.author = author;
        this.genres = genres;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id='" + id + '\'' +
                '}';
    }
}

