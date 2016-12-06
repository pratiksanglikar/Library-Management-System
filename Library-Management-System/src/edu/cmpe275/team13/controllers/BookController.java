package edu.cmpe275.team13.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import edu.cmpe275.team13.beans.Book;
import edu.cmpe275.team13.search.BookRepository;
import edu.cmpe275.team13.search.BookSearch;
import edu.cmpe275.team13.search.BookSpecification;
import edu.cmpe275.team13.service.BookService;

@Controller
@RequestMapping(value = "/books")
public class BookController {

	@Autowired
	private BookService bookservice;

	@Autowired
	private BookRepository<Book> bookRepository;

	/**
	 * @return the bookservice
	 */
	public BookService getBookservice() {
		return bookservice;
	}

	/**
	 * @param bookservice
	 *            the bookservice to set
	 */
	public void setBookservice(BookService bookservice) {
		this.bookservice = bookservice;
	}

	@RequestMapping(value = "/{isbn}", method = RequestMethod.GET)
	public String getBook(@PathVariable long isbn, Model model) {
		Book book = bookservice.getBookById(isbn);
		model.addAttribute("book", book);
		return "book/showbook";
	}

	@RequestMapping(value = "librarian/{isbn}", method = RequestMethod.GET)
	public String getBookLibrarian(@PathVariable long isbn, Model model) {
		Book book = bookservice.getBookById(isbn);
		model.addAttribute("book", book);
		return "book/updatebook";
	}

	@RequestMapping(value = "/{isbn}", method = RequestMethod.DELETE)
	public String deleteBook(@PathVariable Long isbn) {
		this.bookservice.removeBook(isbn);
		return "redirect:/index.jsp";
	}

	@RequestMapping(value = "/{isbn}", method = RequestMethod.POST)
	public String updateBook(@PathVariable Long isbn, @ModelAttribute Book book) {
		Book book_db = this.bookservice.getBookById(isbn);
		if (null == book_db) {
			// return createBook(book); TODO
			return null;
		}
		// ASSUMED book status will update in business logic according to
		// checkout
		book_db.setAuthor_name(book.getAuthor_name());
		// book_db.setAvailable_copies(book.getAvailable_copies()); ASSUMED
		// Business Logic will take care while checkout
		book_db.setTitle(book.getTitle());
		book_db.setCall_number(book.getCall_number());
		book_db.setPublisher_name(book.getPublisher_name());
		book_db.setYear_of_publication(book.getYear_of_publication());
		book_db.setLocation_in_library(book.getLocation_in_library());
		book_db.setNumber_of_copies(book.getNumber_of_copies());
		book_db.setImage(book.getImage());
		// book_db.setUpdated_by(book.getUpdated_by()); TODO take it from
		// currently logged in user.
		this.bookservice.updateBook(book_db);
		return "redirect:/books/librarian/" + isbn;
	}

	@RequestMapping(method = RequestMethod.GET)
	public String listBooks() {
		return "book/createbook";
	}

	@RequestMapping(value = "/searchbook", method = RequestMethod.GET)
	public String search(@RequestParam Map<String, String> map) {
		/*BookSearch booksearch = new BookSearch(map.get("title"), map.get("author_name"), map.get("publisher_name"),
				Long.parseLong(map.get("isbn")), null, true, Integer.parseInt(map.get("created_by")), Integer.parseInt(map.get("updated_by")));
				*/
		BookSearch booksearch = new BookSearch(null, null, null,
				Long.parseLong(map.get("isbn")), null, true, Integer.MIN_VALUE, Integer.MIN_VALUE);
		BookSpecification bookSpec = new BookSpecification(booksearch);
		List<Book> books = bookRepository.findAll(bookSpec);
		// mav.addObject(bookSearch);
		// mav.addObject("books",books);
		// System.out.println();
		// return mav;
		System.out.println(books.size());
		return "redirect:/books/search";
	}

	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public String searchBook() {
		return "book/searchbook";
	}

	/*
	 * creates a new book in the system
	 */
	@RequestMapping(method = RequestMethod.POST)
	public String createBook(@ModelAttribute Book book) {
		Long isbn = this.bookservice.addBook(book);
		return "redirect:/books/" + isbn;
	}
}
