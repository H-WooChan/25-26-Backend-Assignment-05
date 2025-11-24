package com.gdg.oauthgoogleloginexample.dto;

import com.gdg.oauthgoogleloginexample.domain.Book;

public record BookDto(
        Long id,
        String title,
        String publisher
) {
    public static BookDto from(Book book) {
        return new BookDto(book.getId(), book.getTitle(), book.getPublisher());
    }
}
