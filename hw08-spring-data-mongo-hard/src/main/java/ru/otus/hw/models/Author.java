package ru.otus.hw.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@NoArgsConstructor
@Data
public class Author {

    @Id
    @EqualsAndHashCode.Exclude
    private String id;

    @Indexed(unique = true)
    private String uuid;

    private String fullName;

    public Author(String id, String uuid, String fullName) {
        this.id = id;
        this.uuid = uuid;
        this.fullName = fullName;
    }

    public Author(String uuid, String fullName) {
        this.uuid = uuid;
        this.fullName = fullName;
    }

    @Override
    public String toString() {
        return "Author{" +
                "id='" + id + '\'' +
                '}';
    }
}
