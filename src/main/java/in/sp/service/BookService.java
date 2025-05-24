package in.sp.service;

import in.sp.model.Book;
import in.sp.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;          // ✅ Import
import org.springframework.data.domain.Pageable;      // ✅ Import
import org.springframework.data.domain.PageRequest;   // ✅ Import
import org.springframework.data.domain.Sort;          // ✅ Import
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;
    
    private static final String UPLOAD_DIR = "src/main/resources/static/images/";

    // 1. Get all books (existing)
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    // 2. Add a book (existing)
    public Book addBook(Book book) {
        return bookRepository.save(book);
    }

    // 3. Search books (existing)
    public List<Book> searchBooks(String query) {
        return bookRepository.findByTitleContainingOrAuthorContainingOrIsbnContaining(query, query, query);
    }

    // 4. Get paginated & sorted books (NEW)
    public Page<Book> getPaginatedBooks(int page, String sortField, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ?
                Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(page, 5, sort); // 5 items per page
        return bookRepository.findAll(pageable);
    }

    // Get a book by ID
    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid book Id: " + id));
    }

    // Update book while retaining existing image
    public void updateBook(Long id, Book updatedBook, MultipartFile file) {
        Book existingBook = getBookById(id);

        existingBook.setTitle(updatedBook.getTitle());
        existingBook.setAuthor(updatedBook.getAuthor());
        existingBook.setIsbn(updatedBook.getIsbn());

        if (file != null && !file.isEmpty()) {
            try {
                String fileName = file.getOriginalFilename();
                Path path = Paths.get(UPLOAD_DIR + fileName);
                Files.write(path, file.getBytes());
                existingBook.setCoverImage(fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        bookRepository.save(existingBook);
    }

    // 7. Delete book (NEW)
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }
}
