package com.example.polybookbe.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class BookCategoryId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "book_id")
    private Integer bookId;

    @Column(name = "category_id")
    private Integer categoryId;
}
