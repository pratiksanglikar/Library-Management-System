package edu.cmpe275.team13.persistence;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import edu.cmpe275.team13.beans.Book;
import edu.cmpe275.team13.beans.BookStatus;
import edu.cmpe275.team13.beans.IssueBook;
import edu.cmpe275.team13.beans.IssueBookID;
import edu.cmpe275.team13.beans.Patron;
import edu.cmpe275.team13.beans.Reservation;
import edu.cmpe275.team13.beans.ReservationId;
import edu.cmpe275.team13.beans.Sample;
import edu.cmpe275.team13.beans.Transaction;
import edu.cmpe275.team13.beans.Waitlist;
import edu.cmpe275.team13.beans.WaitlistId;
import edu.cmpe275.team13.exceptions.BookNotFoundException;
import edu.cmpe275.team13.exceptions.RenewLimitExceeded;
import edu.cmpe275.team13.service.BookService;
import edu.cmpe275.util.Mailmail;

@Service
public class TransactionDAOImpl implements TransactionDAO {

	Mailmail mail = new Mailmail();

	@Autowired
	private PatronDAOImpl patronDAO;
	
	@Autowired 
	private BookService bookService;

	@Override
	public void performTransaction(Transaction transaction) {
		if (transaction.isCheckout()) {
			performCheckoutTransaction(transaction);
		} else {
			performReturnTransaction(transaction);
		}
	}

	private void performReturnTransaction(Transaction transaction) {
		EntityManager em = EMF.get().createEntityManager();
		EntityTransaction tx = em.getTransaction();
		List<Book> books = transaction.getBooks();
		StringBuilder mailContents = new StringBuilder();
		mailContents.append("Hi, " + transaction.getPatron().getPatron_name() + ", \r");
		mailContents.append(
				"Thank you for using Team - 13 Library Management System. Here's the summary of your recent transaction: \r\r");
		mailContents.append("\r\r Transaction Date: " + new java.util.Date().toLocaleString());
		mailContents.append("\r\rFollowing books were returned recently: ");
		tx.begin();
		int i = 1;
		try {
			List<IssueBook> booksPending = getPendingBooks(transaction.getPatron().getPatron_id());
			long totalFine = 0;
			for (IssueBook issueBook : booksPending) {
				for (Book book : books) {
					if (book.getIsbn().equals(issueBook.getId().getIsbn())) {
						Timestamp actualReturnDate = new Timestamp(new java.util.Date().getTime());
						issueBook.setActual_return_date(actualReturnDate);
						long difference = actualReturnDate.getTime() - issueBook.getDue_date().getTime();
						long hours = 0 ;
						if (difference > 0) {
							hours = difference / (3600 * 1000);
							hours = hours/24;
						}
						mailContents.append("\r\r" + i + ") Book: " + book.getTitle() + "\t\tCheckout Date:"
								+ issueBook.getId().getIssue_date().toLocaleString() + "\t\t Return Date: "
								+ issueBook.getActual_return_date().toLocaleString() + 
								"\t\t Fine: $" + hours);
						i++;
						issueBook.setFine((int) hours);
						totalFine += hours;
						em.merge(issueBook);
					}
				}
			}
			mailContents.append("Total Fine: " + totalFine);
			for (Book book : books) {
				if (isWaitlisted(book, transaction.getPatron())) {
					book.setBook_status(BookStatus.WAITLIST);
				} else {
					book.setBook_status(BookStatus.AVAILABLE);
					book.setAvailable_copies(book.getAvailable_copies() + 1);
				}
				em.merge(book);
			}
			Patron patron = transaction.getPatron();
			patron.setBooks_issued(patron.getBooks_issued() - books.size());
			em.merge(patron);
			mailContents.append("\r\rThank you!");
			sendMail(transaction.getPatron().getPatron_email(),
					"Team - 13 - Library Management - Summary of recent return transaction", mailContents.toString());
			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			tx.rollback();
		} finally {
			em.close();
		}
	}

	private boolean isWaitlisted(Book book, Patron patron) {
		System.out.println("Searching book for waitlist ... " + book.getTitle());
		EntityManager em = EMF.get().createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		boolean isWaitlisted = false;
		try {
			Query query = em.createQuery("SELECT e FROM Waitlist e WHERE e.id.isbn = :isbn ORDER BY e.join_date");
			query.setParameter("isbn", book.getIsbn());
			@SuppressWarnings("unchecked")
			List<Waitlist> list = query.getResultList();
			isWaitlisted = list.size() > 0;
			Timestamp ts = new Timestamp(new java.util.Date().getTime());
			Calendar cal = Calendar.getInstance();
			cal.setTime(ts);
			cal.add(Calendar.DATE, 3);
			Timestamp endTs = new Timestamp(cal.getTime().getTime());
			if (isWaitlisted) {
				Patron waitlisted = this.patronDAO.getPatron(list.get(0).getId().getPatron_id());
				Reservation reservation = new Reservation(
						new ReservationId(book.getIsbn(), waitlisted.getPatron_id(), ts), endTs, false);
				String messgae = "The book that you were in waitlist - " + book.getTitle()
						+ " - is now available. You can checkout this book before " + endTs.toLocaleString() + ".";

				sendMail(waitlisted.getPatron_email(), "Book available", messgae);
				em.merge(reservation);
				em.remove(list.get(0));
				Query query1 = em
						.createQuery("DELETE FROM Waitlist e WHERE e.id.isbn = :isbn AND e.id.patron_id = :pid");
				System.out.println("ISBN: " + book.getIsbn());
				System.out.println("PID: " + waitlisted.getPatron_id());
				query1.setParameter("isbn", book.getIsbn());
				query1.setParameter("pid", waitlisted.getPatron_id());
				query1.executeUpdate();
			}
			tx.commit();
		} catch (Exception e) {
			tx.rollback();
			e.printStackTrace();
			System.out.println(e.getMessage());
		} finally {
			em.close();
		}
		return isWaitlisted;
	}

	@Override
	public List<IssueBook> getPendingBooks(int patron_id) {
		EntityManager em = EMF.get().createEntityManager();
		Query query = em.createQuery(
				"SELECT e FROM IssueBook e WHERE e.id.patron_id = :patron_id AND e.actual_return_date IS NULL");
		query.setParameter("patron_id", patron_id);
		@SuppressWarnings("unchecked")
		List<IssueBook> list = query.getResultList();
		em.close();
		return list;
	}

	@SuppressWarnings("null")
	private void performCheckoutTransaction(Transaction transaction) {
		System.out.println("In checkoutTransaction()");
		EntityManager em = EMF.get().createEntityManager();
		EntityTransaction tx = em.getTransaction();
		List<Book> books = transaction.getBooks();
		List<Book> checkoutbooks = getCheckoutBooks(books, transaction.getPatron());
		List<Book> waitlistBooks = getWaitlistBooks(books, transaction.getPatron());
		tx.begin();
		StringBuilder mailContents = new StringBuilder();
		mailContents.append("Hi, " + transaction.getPatron().getPatron_name() + ", \r");
		mailContents.append(
				"Thank you for using Team - 13 Library Management System. Here's the summary of your recent transaction: \r\r");
		mailContents.append("\r\r Transaction Date: " + new java.util.Date().toLocaleString());
		try {
			System.out.println("In try - checkout");
			int i = 1;
			for (Book book : checkoutbooks) {
				IssueBook issue = prepareIssueBook(book, transaction.getPatron().getPatron_id());
				Reservation res = getReserved(book, transaction.getPatron().getPatron_id());
				if (null != res) {
					res.setChecked_out(true);
				} else {
					book.setAvailable_copies(book.getAvailable_copies() - 1);
					System.out.println(" No Reservation");
				}
				if (book.getAvailable_copies() == 0) {
					book.setBook_status(BookStatus.WAITLIST);
				}
				em.merge(issue);
				em.merge(book);
				if (null != res) {
					em.merge(res);
				}
				mailContents.append("\r\r" + i + ") Book: " + book.getTitle() + "\t\tCheckout Date:"
						+ issue.getId().getIssue_date().toLocaleString() + "\t\t Due Date: "
						+ issue.getDue_date().toLocaleString());
				i++;
			}
			Patron patron = transaction.getPatron();
			patron.setBooks_issued(patron.getBooks_issued() + checkoutbooks.size());
			em.merge(patron);
			List<Waitlist> list = getWaitlistEntries(waitlistBooks, patron);
			for (Waitlist waitlist : list) {
				em.merge(waitlist);
				mailContents.append("\r\r" + i + ") Book: " + waitlist.getId().getIsbn() + "\t\tJoined Waitlist!");
			}
			tx.commit();
			mailContents.append("\r\rThank you!");
			sendMail(transaction.getPatron().getPatron_email(),
					"Team - 13 - Library Management - Summary of recent checkout transaction", mailContents.toString());
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			tx.rollback();
		} finally {
			em.close();
		}
	}

	private Reservation getReserved(Book book, int patron_id) {
		EntityManager em = EMF.get().createEntityManager();
		Query query = em.createQuery(
				"select e FROM Reservation e WHERE e.id.isbn = :isbn AND e.id.patron_id = :p_id AND e.checked_out = false  AND e.end_date > :date");
		query.setParameter("isbn", book.getIsbn());
		query.setParameter("p_id", patron_id);
		query.setParameter("date", new Timestamp(new java.util.Date().getTime()));
		Reservation res = null;
		try {
			res = (Reservation) query.getSingleResult();
		} catch (NoResultException e) {
			System.out.println("No Reservation found!");
			return null;
		}
		return res;
	}

	/**
	 * 
	 * @param waitlistBooks
	 * @param patron
	 */
	private List<Waitlist> getWaitlistEntries(List<Book> waitlistBooks, Patron patron) {

		List<Waitlist> list = new ArrayList<Waitlist>(0);
		for (Book book : waitlistBooks) {
			WaitlistId id = new WaitlistId(book.getIsbn(), patron.getPatron_id());
			Waitlist wl = new Waitlist(id, new Timestamp(new java.util.Date().getTime()));
			list.add(wl);
		}
		return list;
	}

	/**
	 * 
	 * @param books
	 * @return
	 */
	private List<Book> getWaitlistBooks(List<Book> books, Patron patron) {
		List<Book> checkoutbook = new ArrayList<Book>(0);
		for (Book book : books) {
			if (book.getBook_status() == BookStatus.WAITLIST && !isReserved(book, patron)) {
				checkoutbook.add(book);
			}
		}
		return checkoutbook;
	}

	private List<Book> getCheckoutBooks(List<Book> books, Patron patron) {
		List<Book> checkoutbook = new ArrayList<Book>(0);
		for (Book book : books) {
			if (isReserved(book, patron)) {
				checkoutbook.add(book);
			}
			if (book.getBook_status() == BookStatus.AVAILABLE) {
				checkoutbook.add(book);
			}
		}
		return checkoutbook;
	}

	private boolean isReserved(Book book, Patron patron) {
		EntityManager em = EMF.get().createEntityManager();
		Query query = em.createQuery(
				"SELECT e FROM Reservation e WHERE e.id.isbn = :isbn AND e.id.patron_id = :p_id AND e.checked_out = FALSE AND e.end_date > :date");
		query.setParameter("isbn", book.getIsbn());
		query.setParameter("p_id", patron.getPatron_id());
		query.setParameter("date", new Timestamp(new java.util.Date().getTime()));
		Reservation reservation = null;
		try {
			reservation = (Reservation) query.getSingleResult();
		} catch (NoResultException e) {
			return false;
		}
		if (reservation == null) {
			return false;
		}
		return true;
	}

	private void sendMail(String to, String subject, String mailContents) {
		String sender = "librarymanagement275@gmail.com";
		mail.sendMail(sender, to, subject, mailContents);
	}

	private IssueBook prepareIssueBook(Book book, int patron_id) {
		Timestamp issue_date = new Timestamp(new java.util.Date().getTime());
		Calendar cal = Calendar.getInstance();
		cal.setTime(issue_date);
		cal.add(Calendar.DATE, 30);
		Timestamp due_date = new Timestamp(cal.getTime().getTime());
		IssueBook issue = new IssueBook(new IssueBookID(book.getIsbn(), patron_id, issue_date), due_date, null, 0);
		return issue;
	}

	@Override
	public int getTodaysTransaction(int patron_id) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		EntityManager em = EMF.get().createEntityManager();
		Query query = em.createQuery("SELECT e FROM IssueBook e WHERE e.id.issue_date LIKE \'%"
				+ df.format(new java.util.Date()) + "%\'" + " AND e.id.patron_id = :patron_id");
		// query.setParameter("issue_date", df.format(new java.util.Date()) +
		// "%");
		query.setParameter("patron_id", patron_id);
		int count = query.getResultList().size();
		em.close();
		return count;
	}

	@Override
	public List<Waitlist> getWaitlistedBooks(int patron_id) {
		EntityManager em = EMF.get().createEntityManager();
		Query query = em.createQuery("SELECT e FROM Waitlist e WHERE e.id.patron_id = :patron_id");
		query.setParameter("patron_id", patron_id);
		@SuppressWarnings("unchecked")
		List<Waitlist> waitlisted = query.getResultList();
		return waitlisted;
	}

	@Override
	public void renewBook(Long isbn, int patron_id) {
		Book book = this.bookService.getBookById(isbn);
		EntityManager em = EMF.get().createEntityManager();
		EntityTransaction tx = em.getTransaction();
		Patron patron = this.patronDAO.getPatron(patron_id);
		StringBuilder sb = new StringBuilder();
		sb.append("Hi " + patron.getPatron_name() + ", " + "\r\r");
		if(book.getBook_status() == BookStatus.AVAILABLE) {
			Query query = em.createQuery("SELECT e from IssueBook e WHERE e.id.isbn = :isbn AND e.id.patron_id = :patron_id");
			query.setParameter("isbn", book.getIsbn());
			query.setParameter("patron_id", patron_id);
			IssueBook issue = null;
			try {
				issue = (IssueBook) query.getSingleResult();
			} catch(NoResultException e) {
				throw new BookNotFoundException();
			}
			long difference = issue.getDue_date().getTime() - issue.getId().getIssue_date().getTime();
			long days = difference / (24 * 3600 * 1000);
			if(days > 60) {
				throw new RenewLimitExceeded();
			}
			Timestamp ts = new Timestamp(issue.getDue_date().getTime());
			Calendar cal = Calendar.getInstance();
			cal.setTime(ts);
			cal.add(Calendar.DATE, 30);
			Timestamp renewedTimestamp = new Timestamp(cal.getTime().getTime());
			issue.setDue_date(renewedTimestamp);
			try {
				tx.begin();
				em.merge(issue);
				sb.append("The book " + book.getTitle() + " has been renewed for another 30 days. Your new due date is: " + renewedTimestamp.toLocaleString());
				tx.commit();
				sendMail(patron.getPatron_email(), "Book Renewal Confirmation", sb.toString());
			} catch(Exception e) {
				tx.rollback();
			} finally {
				em.close();
			}
		} else {
			throw new RenewLimitExceeded();
		}
	}

	@Override
	public void updateReservations(Long isbn, int patron_id) {
/*		EntityManager em = EMF.get().createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		try {
			Sample sample = new Sample();
			sample.setId((int) new java.util.Date().getTime()/10000);
			sample.setValue((int) new java.util.Date().getTime()/10000);
			em.merge(sample);
			tx.commit();
		} catch(Exception e) {
			System.out.println(e.getMessage());
			tx.rollback();
		} finally {
			em.close();
		}
*/	
		Book book = this.bookService.getBookById(isbn);
		EntityManager em = EMF.get().createEntityManager();
		EntityTransaction tx = em.getTransaction();
		Patron patron = this.patronDAO.getPatron(patron_id);
		EntityManager em1 = EMF.get().createEntityManager();
		EntityTransaction tx1 = em.getTransaction();
		Query query = em.createQuery("SELECT e from Reservation e WHERE e.id.isbn = :isbn AND e.id.patron_id = :patron_id");
		query.setParameter("isbn", book.getIsbn());
		query.setParameter("patron_id", patron_id);
		Reservation reservation = null;
		try {
			reservation = (Reservation) query.getSingleResult();
		} catch(NoResultException e) {
			throw new BookNotFoundException();
		}
		Timestamp ts = new Timestamp(reservation.getId().getStart_date().getTime());
		
	}
}
