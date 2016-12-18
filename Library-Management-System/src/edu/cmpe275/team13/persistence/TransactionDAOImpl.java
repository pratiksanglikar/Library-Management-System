package edu.cmpe275.team13.persistence;

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
import org.springframework.stereotype.Service;

import edu.cmpe275.team13.beans.Book;
import edu.cmpe275.team13.beans.BookStatus;
import edu.cmpe275.team13.beans.IssueBook;
import edu.cmpe275.team13.beans.IssueBookID;
import edu.cmpe275.team13.beans.Patron;
import edu.cmpe275.team13.beans.Reservation;
import edu.cmpe275.team13.beans.ReservationId;
import edu.cmpe275.team13.beans.Transaction;
import edu.cmpe275.team13.beans.Waitlist;
import edu.cmpe275.team13.beans.WaitlistId;
import edu.cmpe275.util.Mailmail;

@Service
public class TransactionDAOImpl implements TransactionDAO {

	Mailmail mail = new Mailmail();

	@Autowired
	private PatronDAOImpl patronDAO;

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
			for (IssueBook issueBook : booksPending) {
				for (Book book : books) {
					if (book.getIsbn().equals(issueBook.getId().getIsbn())) {
						issueBook.setActual_return_date(new Timestamp(new java.util.Date().getTime()));
						// TODO calculate fine.
						mailContents.append("\r\r" + i + ") Book: " + book.getTitle() + "\t\tCheckout Date:"
								+ issueBook.getId().getIssue_date().toLocaleString() + "\t\t Return Date: "
								+ issueBook.getActual_return_date().toLocaleString());
						i++;
						em.merge(issueBook);
					}
				}
			}
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
				Reservation reservation = new Reservation(
						new ReservationId(book.getIsbn(), list.get(0).getId().getPatron_id(), ts), endTs, false);
				String messgae = "The book that you were in waitlist - " + book.getTitle()
						+ " - is now available. You can checkout this book before " + endTs.toLocaleString() + ".";
				Patron waitlisted = this.patronDAO.getPatron(list.get(0).getId().getPatron_id());
				sendMail(waitlisted.getPatron_email(), "Book available", messgae);
				em.merge(reservation);
				em.remove(list.get(0));
			}
			tx.commit();
		} catch (Exception e) {
			tx.rollback();
			e.printStackTrace();
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

	private void performCheckoutTransaction(Transaction transaction) {
		EntityManager em = EMF.get().createEntityManager();
		EntityTransaction tx = em.getTransaction();
		List<Book> books = transaction.getBooks();
		List<Book> checkoutbooks = getCheckoutBooks(books, transaction.getPatron());
		List<Book> waitlistBooks = getWaitlistBooks(books);
		tx.begin();
		StringBuilder mailContents = new StringBuilder();
		mailContents.append("Hi, " + transaction.getPatron().getPatron_name() + ", \r");
		mailContents.append(
				"Thank you for using Team - 13 Library Management System. Here's the summary of your recent transaction: \r\r");
		mailContents.append("\r\r Transaction Date: " + new java.util.Date().toLocaleString());
		try {
			int i = 1;
			for (Book book : checkoutbooks) {
				IssueBook issue = prepareIssueBook(book, transaction.getPatron().getPatron_id());
				Reservation res = getReserved(book, transaction.getPatron().getPatron_id()); 
				if (null != res) {
					book.setAvailable_copies(book.getAvailable_copies() - 1);
				} else {
					res.setChecked_out(true);
				}
				if (book.getAvailable_copies() == 0) {
					book.setBook_status(BookStatus.WAITLIST);
				}
				em.merge(issue);
				em.merge(book);
				em.merge(res);
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
			e.printStackTrace();
			tx.rollback();
		} finally {
			em.close();
		}
	}

	private Reservation getReserved(Book book, int patron_id) {
		EntityManager em = EMF.get().createEntityManager();
		Query query = em.createQuery("select e FROM Reservation e WHERE e.id.isbn = :isbn AND e.id.patron_id = :p_id AND e.checked_out = FALSE");
		query.setParameter("isbn", book.getIsbn());
		query.setParameter("p_id", patron_id);
		Reservation res = null;
		try {
			res = (Reservation) query.getSingleResult();
		} catch (NoResultException e) {
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
	private List<Book> getWaitlistBooks(List<Book> books) {
		List<Book> checkoutbook = new ArrayList<Book>(0);
		for (Book book : books) {
			if (book.getBook_status() == BookStatus.WAITLIST) {
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
		Query query = em.createQuery("SELECT e FROM Reservation e WHERE e.id.isbn = :isbn AND e.id.patron_id = :p_id AND e.checked_out = FALSE");
		query.setParameter("isbn", book.getIsbn());
		query.setParameter("p_id", patron.getPatron_id());
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
		IssueBook issue = new IssueBook(new IssueBookID(book.getIsbn(), patron_id, issue_date), due_date, null);
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
}
