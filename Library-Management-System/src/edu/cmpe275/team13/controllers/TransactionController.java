package edu.cmpe275.team13.controllers;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.cmpe275.team13.beans.AppSettings;
import edu.cmpe275.team13.beans.Book;
import edu.cmpe275.team13.beans.IssueBook;
import edu.cmpe275.team13.beans.Patron;
import edu.cmpe275.team13.beans.Transaction;
import edu.cmpe275.team13.beans.Waitlist;
import edu.cmpe275.team13.exceptions.UnauthorizedAccessException;
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
	
	@Autowired
	private AppSettings appSettings;

	@RequestMapping(value = "/return", method = RequestMethod.GET)
	public String returnBooks(HttpSession session) {
		if(null == session || session.getAttribute("type") == null) {
			return "login";
		}
		if (session.getAttribute("type").equals("librarian")) {
			throw new UnauthorizedAccessException();
		}
		@SuppressWarnings("unchecked")
		List<Long> book_list = (List<Long>) session.getAttribute("book_list");
		Set<Long> unique = new HashSet<Long>(book_list);
		book_list = new ArrayList<Long>(unique);
		Patron patron = this.patronService.getPatron((int) session.getAttribute("user_id"));
		Transaction transaction = prepareTransaction(patron, book_list, false);
		transactionService.performTransaction(transaction);
		session.setAttribute("book_list", new ArrayList<Long>(0));
		return "redirect:/transaction/summary";
	}
	
	@RequestMapping(value = "/updatereservation", method = RequestMethod.GET)
	public ResponseEntity<Void> updateReservations2() {
		this.transactionService.updateReservations();
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@RequestMapping(value = "/emailupdates", method = RequestMethod.GET)
	public ResponseEntity<Void> updateEmail() {
		this.transactionService.updateEmail();
		return new ResponseEntity<Void>(HttpStatus.OK);
	}
	
	

	@RequestMapping(value = "/checkout", method = RequestMethod.GET)
	public String checkoutBooks(HttpSession session) {
		if(null == session || session.getAttribute("type") == null) {
			return "login";
		}
		if (session.getAttribute("type").equals("librarian")) {
			throw new UnauthorizedAccessException();
		}
		@SuppressWarnings("unchecked")
		List<Long> book_list = (List<Long>) session.getAttribute("book_list");
		Set<Long> unique = new HashSet<Long>(book_list);
		book_list = new ArrayList<Long>(unique);
		Patron patron = this.patronService.getPatron((int) session.getAttribute("user_id"));
		Transaction transaction = prepareTransaction(patron, book_list, true);
		transactionService.performTransaction(transaction);
		session.setAttribute("book_list", new ArrayList<Long>(0));
		return "redirect:/transaction/summary";
	}
	
	@RequestMapping(value = "/summary", method = RequestMethod.GET)
	public String getSummary(HttpSession session, Model model) {
		if(null == session || session.getAttribute("type") == null) {
			return "login";
		}
		if(session.getAttribute("type").equals("librarian")) {
			throw new UnauthorizedAccessException();
		}
		int patron_id = (int) session.getAttribute("user_id");
		Patron patron = this.patronService.getPatron(patron_id);
		List<IssueBook> issue_books = this.transactionService.getPendingBooks(patron_id);
		List<Book> books = prepareBooks(issue_books);
		List<Book> waitlisted_books = getWaitlistedBooks(patron_id);
		model.addAttribute("patron", patron);
		model.addAttribute("issue_books", issue_books);
		model.addAttribute("books", books);
		model.addAttribute("waitlist", waitlisted_books);
		model.addAttribute("date", appSettings.getAppDate());
		System.out.println(appSettings.getAppDate());
		return "patrondashboard";
	}

	private List<Book> getWaitlistedBooks(int patron_id) {
		List<Waitlist> waitlisted = this.transactionService.getWaitlistedBooks(patron_id);
		List<Book> books = new ArrayList<Book>(0);
		for (Waitlist waitlist : waitlisted) {
			books.add(this.bookservice.getBookById(waitlist.getId().getIsbn()));
		}
		return books;
	}

	private List<Book> prepareBooks(List<IssueBook> issue_books) {
		List<Book> books = new ArrayList<Book>(0);
		for (IssueBook issueBook : issue_books) {
			books.add(this.bookservice.getBookById(issueBook.getId().getIsbn()));
		}
		return books;
	}

	private Transaction prepareTransaction(Patron patron, List<Long> book_list, boolean isCheckout) {
		List<Book> books = new ArrayList<Book>(0);
		for (Long isbn : book_list) {
			books.add(bookservice.getBookById(isbn));
		}
		Transaction transaction = new Transaction(books, patron, new Date(appSettings.getAppDate().getTime()), isCheckout);
		return transaction;
	}

	/**
	 * @return the transactionService
	 */
	public TransactionService getTransactionService() {
		return transactionService;
	}

	/**
	 * @param transactionService
	 *            the transactionService to set
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
	 * @param bookservice
	 *            the bookservice to set
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
	 * @param patronService
	 *            the patronService to set
	 */
	public void setPatronService(PatronDAOImpl patronService) {
		this.patronService = patronService;
	}
}