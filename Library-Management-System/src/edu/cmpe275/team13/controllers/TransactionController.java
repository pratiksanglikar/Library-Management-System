package edu.cmpe275.team13.controllers;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.cmpe275.team13.beans.Book;
import edu.cmpe275.team13.beans.Patron;
import edu.cmpe275.team13.beans.Transaction;
import edu.cmpe275.team13.persistence.PatronDAOImpl;
import edu.cmpe275.team13.service.BookService;
import edu.cmpe275.team13.service.TransactionService;

@Controller
@RequestMapping(value = "/transaction")
public class TransactionController {
	
	@Autowired
	private TransactionService transactionService;
	
	@Autowired
	private BookService bookservice;
	
	@Autowired
	private PatronDAOImpl patronService;
	
	@RequestMapping(value = "/return", method = RequestMethod.GET) 
	public String returnBooks(/*HttpSession session*/) {
		/*if(session.getAttribute("type").equals("librarian")) {
		throw new UnauthorizedAccessException();
		}*/
		//@SuppressWarnings("unchecked")
		/*List<Long> book_list = (List<Long>) session.getAttribute("book_list");
		Patron patron = (Patron) session.getAttribute("user");*/
		List<Long> book_list = Arrays.asList(new Long[]{9780596808334L,9781449399917L});
		Patron patron = this.patronService.getPatron(787878);
		Transaction transaction = prepareTransaction(patron, book_list, false);
		transactionService.performTransaction(transaction);
		return "redirect:/books/search"; // TODO
	}

	@RequestMapping(value = "/checkout", method = RequestMethod.GET) 
	public String checkoutBooks(/*HttpSession session*/) {
		/*if(session.getAttribute("type").equals("librarian")) {
			throw new UnauthorizedAccessException();
		}*/
		//@SuppressWarnings("unchecked")
		/*List<Long> book_list = (List<Long>) session.getAttribute("book_list");
		Patron patron = (Patron) session.getAttribute("user");*/
		List<Long> book_list = Arrays.asList(new Long[]{9780596808334L,9781449399917L});
		Patron patron = this.patronService.getPatron(787878);
		Transaction transaction = prepareTransaction(patron, book_list, true);
		transactionService.performTransaction(transaction);
		return "redirect:/books/search"; // TODO
	}

	private Transaction prepareTransaction(Patron patron, List<Long> book_list, boolean isCheckout) {
		List<Book> books = new ArrayList<Book>(0);
		for (Long isbn : book_list) {
			books.add(bookservice.getBookById(isbn));
		}
		Transaction transaction = new Transaction(books, patron, new Date(new java.util.Date().getTime()), isCheckout);
		return transaction;
	}
	
	
	/**
	 * @return the transactionService
	 */
	public TransactionService getTransactionService() {
		return transactionService;
	}

	/**
	 * @param transactionService the transactionService to set
	 */
	public void setTransactionService(TransactionService transactionService) {
		this.transactionService = transactionService;
	}

	/**
	 * @return the bookservice
	 */
	public BookService getBookservice() {
		return bookservice;
	}

	/**
	 * @param bookservice the bookservice to set
	 */
	public void setBookservice(BookService bookservice) {
		this.bookservice = bookservice;
	}

	/**
	 * @return the patronService
	 */
	public PatronDAOImpl getPatronService() {
		return patronService;
	}

	/**
	 * @param patronService the patronService to set
	 */
	public void setPatronService(PatronDAOImpl patronService) {
		this.patronService = patronService;
	}
}
