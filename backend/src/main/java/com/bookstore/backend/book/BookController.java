package com.bookstore.backend.book;

import com.bookstore.backend.book.dto.BookRequestDto;
import com.bookstore.backend.core.cloudinary.CloudinaryUtils;
import com.cloudinary.Cloudinary;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(value = "api/books", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
class BookController {

    private final BookService bookService;
    private final Cloudinary cloudinary;

    @GetMapping
    Page<Book> all(Pageable pageable) {
        return bookService.findAll(pageable);
    }

    @GetMapping("/{isbn}")
    Book byIsbn(@PathVariable String isbn) {
        return bookService.findByIsbn(isbn);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Book save(@Valid @ModelAttribute BookRequestDto bookRequestDto) {
        Book book = bookRequestDto.convertToBook(cloudinary);
        return bookService.save(book);
    }

    @PatchMapping(value = "/{isbn}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Book update(@PathVariable String isbn, BookRequestDto bookRequestDto) {
        var book = bookRequestDto.convertToBook(cloudinary);
        return bookService.updateByIsbn(isbn, book);
    }

    @PutMapping(value = "/{isbn}/photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Book uploadPhoto(@PathVariable String isbn, @ModelAttribute List<MultipartFile> photos) {
        Book book = bookService.findByIsbn(isbn);
        var oldPhotos = book.getPhotos();
        // clear old photos
        if (oldPhotos!= null && !oldPhotos.isEmpty()) {
            book.getPhotos().forEach(photoUrl -> {
                try {
                    CloudinaryUtils.deleteFile(CloudinaryUtils.convertUrlToPublicId(photoUrl), cloudinary);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        List<String> urls = CloudinaryUtils.convertListMultipartFileToListUrl(photos, cloudinary);
        book.setPhotos(urls);
        return bookService.save(book);
    }

    @DeleteMapping("/{isbn}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable String isbn) {
        bookService.deleteByIsbn(isbn);
    }

}