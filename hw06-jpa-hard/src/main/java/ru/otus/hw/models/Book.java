package ru.otus.hw.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "books")
@NamedEntityGraph(name = "books-author-entity-graph",
        attributeNodes = {@NamedAttributeNode("author")})
@Data
@NoArgsConstructor
public class Book {

    public Book(long id, String title, Author author, List<Genre> genres) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.genres = genres;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String title;

    @ManyToOne(targetEntity = Author.class)
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;

    @ManyToMany(targetEntity = Genre.class, fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(name = "books_genres", joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id"))
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<Genre> genres;

    @OneToMany(targetEntity = BookComment.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "book_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<BookComment> comments;
}

