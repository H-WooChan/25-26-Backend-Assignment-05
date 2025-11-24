package com.gdg.oauthgoogleloginexample.service;

import com.gdg.oauthgoogleloginexample.domain.Book;
import com.gdg.oauthgoogleloginexample.domain.User;
import com.gdg.oauthgoogleloginexample.dto.BookDto;
import com.gdg.oauthgoogleloginexample.repository.BookRepository;
import com.gdg.oauthgoogleloginexample.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Transactional
    public BookDto createBook(Long userId, BookDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다. id=" + userId));

        Book book = Book.builder()
                .title(requestDto.title())
                .publisher(requestDto.publisher())
                .registeredBy(user)
                .build();

        Book savedBook = bookRepository.save(book);
        return BookDto.from(savedBook);
    }

    public BookDto findBookById(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("해당 책을 찾을 수 없습니다. id=" + bookId));
        return BookDto.from(book);
    }

    public Page<BookDto> findAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable)
                .map(BookDto::from);
    }

    @Transactional
    public BookDto updateBook(Long userId, Long bookId, BookDto requestDto) {
        Book book = findBookAndValidateOwner(bookId, userId);
        book.update(requestDto.title(), requestDto.publisher());
        return BookDto.from(book);
    }

    @Transactional
    public void deleteBook(Long userId, Long bookId) {
        Book book = findBookAndValidateOwner(bookId, userId);
        bookRepository.delete(book);
    }

    private Book findBookAndValidateOwner(Long bookId, Long userId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("해당 책을 찾을 수 없습니다. id=" + bookId));

        if (!Objects.equals(book.getRegisteredBy().getId(), userId)) {
            throw new AccessDeniedException("해당 작업에 대한 권한이 없습니다.");
        }
        return book;
    }
}
