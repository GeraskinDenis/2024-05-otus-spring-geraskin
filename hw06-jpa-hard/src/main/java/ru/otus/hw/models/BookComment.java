package ru.otus.hw.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "book_comments")
@NamedEntityGraph(name = "book_comments-book", attributeNodes = {@NamedAttributeNode("book")})
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Book book;

    @Column(nullable = false)
    private String text;
}
