package ru.alikovzaur.library.entityes;

import javax.persistence.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Set;

@Entity
@Table(name = "genre", schema = "library")
public class GenreEntity {
    private long id;
    private String name;
    private Set<BookEntity> bookEntities;

    @Id
    @Column(name = "id")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "genre")
    public Set<BookEntity> getBookEntities() {
        return bookEntities;
    }

    public void setBookEntities(Set<BookEntity> bookEntities) {
        this.bookEntities = bookEntities;
    }
}