package ru.otus.hw.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document(collection = "books")
@Data
@NoArgsConstructor
public class Book {

    @Id
    private String id;

    @Field()
    private String title;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Author author;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<Genre> genres;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<BookComment> comments;

    public Book(String id, String title, Author author, List<Genre> genres) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.genres = genres;
    }
}

