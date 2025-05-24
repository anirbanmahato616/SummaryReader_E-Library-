package in.sp.controller;

import in.sp.model.Book;
import in.sp.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class BookController {

    @Autowired
    private BookService bookService;

    private static String UPLOAD_DIR = "src/main/resources/static/images/";

    // 1. Home with pagination & sorting
    @GetMapping("/index")
    public String home(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "title") String sortField,
                       @RequestParam(defaultValue = "asc") String sortDir,
                       Model model) {

        Page<Book> booksPage = bookService.getPaginatedBooks(page, sortField, sortDir);
        model.addAttribute("books", booksPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", booksPage.getTotalPages());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return "index";
    }
    //user view page
    @GetMapping("/")
    public String index(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "title") String sortField,
                       @RequestParam(defaultValue = "asc") String sortDir,
                       Model model) {

        Page<Book> booksPage = bookService.getPaginatedBooks(page, sortField, sortDir);
        model.addAttribute("books", booksPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", booksPage.getTotalPages());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return "home";
    }

    @GetMapping("/add-book")
    public String addBookForm(Model model) {
        model.addAttribute("book", new Book());
        return "add-book";
    }

    @PostMapping("/add-book")
    public String addBookSubmit(@ModelAttribute Book book,
                                @RequestParam("coverImageFile") MultipartFile file) {
        try {
            if (!file.isEmpty()) {
                String fileName = file.getOriginalFilename();
                Path path = Paths.get(UPLOAD_DIR + fileName);
                Files.write(path, file.getBytes());
                book.setCoverImage(fileName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        bookService.addBook(book);
        return "redirect:/index";
    }

    @GetMapping("/search")
    public String searchBooks(@RequestParam String query, Model model) {
        model.addAttribute("books", bookService.searchBooks(query));
        return "book-list";
    }

    // Show edit book form
    @GetMapping("/edit-book/{id}")
    public String editBookForm(@PathVariable Long id, Model model) {
        Book book = bookService.getBookById(id);
        model.addAttribute("book", book);
        return "edit-book";
    }

    // Process update book request
    @PostMapping("/update-book/{id}")
    public String updateBook(@PathVariable Long id, @ModelAttribute Book updatedBook,
                             @RequestParam("coverImageFile") MultipartFile file) {
        bookService.updateBook(id, updatedBook, file);
        return "redirect:/index";
    }

    @GetMapping("/delete-book/{id}")
    public String deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return "redirect:/index";
    }
}
