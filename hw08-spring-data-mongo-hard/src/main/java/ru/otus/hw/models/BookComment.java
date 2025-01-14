package ru.otus.hw.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "book_comments")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookComment {

    @Id
    private String id;

    @Field(name = "book")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Book book;

    @Field(name = "text")
    private String text;
}
