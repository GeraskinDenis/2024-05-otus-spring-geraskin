package ru.otus.hw.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BookComment {

    @Id
    @EqualsAndHashCode.Exclude
    private String id;

    @Indexed(unique = true)
    private String uuid;

    @DBRef
    private Book book;

    private String text;

    public BookComment(String uuid, Book book, String text) {
        this.uuid = uuid;
        this.book = book;
        this.text = text;
    }

    @Override
    public String toString() {
        return "BookComment{" +
                "id='" + id + '\'' +
                '}';
    }
}
