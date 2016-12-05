package edu.cmpe275.team13.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.cmpe275.team13.beans.Book;
import edu.cmpe275.team13.service.BookService;


@Controller
@RequestMapping(value = "/books")
public class BookController {
	
	@Autowired
	private BookService bookservice;

	@RequestMapping(value = "/{isbn}", method = RequestMethod.GET)
	public ResponseEntity<Book> getBook(@PathVariable long isbn) {
		Book book = bookservice.getBookById(isbn);
		return new ResponseEntity<Book>(book, HttpStatus.OK);
		// 978059
	}
}
