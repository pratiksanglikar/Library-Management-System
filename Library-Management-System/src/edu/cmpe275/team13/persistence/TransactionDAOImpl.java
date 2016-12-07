package edu.cmpe275.team13.persistence;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import org.springframework.stereotype.Service;

import edu.cmpe275.team13.beans.Book;
import edu.cmpe275.team13.beans.IssueBook;
import edu.cmpe275.team13.beans.IssueBookID;
import edu.cmpe275.team13.beans.Patron;
import edu.cmpe275.team13.beans.Transaction;
import edu.cmpe275.util.Mailmail;

@Service
public class TransactionDAOImpl implements TransactionDAO {

	Mailmail mail = new Mailmail();
	
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
		mailContents.append("Thank you for using Team - 13 Library Management System. Here's the summary of your recent transaction: \r\r");
		mailContents.append("\r\r Transaction Date: " + new java.util.Date().toLocaleString());
		mailContents.append("\r\rFollowing books were returned recently: ");
		tx.begin();
		int i = 1;
		try {
			List<IssueBook> booksPending = getBooksPending(transaction.getPatron().getPatron_id());
			for (IssueBook issueBook : booksPending) {
				for (Book book : books) {
					if(book.getIsbn().equals(issueBook.getId().getIsbn())) {
						issueBook.setActual_return_date(new Timestamp(new java.util.Date().getTime()));
						//TODO calculate fine.
						mailContents.append("\r\r" + i + ") Book: " + book.getTitle() + "\t\tCheckout Date:" + issueBook.getId().getIssue_date().toLocaleString() + "\t\t Return Date: " + issueBook.getActual_return_date().toLocaleString());
						i++;
						em.merge(issueBook);
					}
				}
			}
			for (Book book : books) {
				book.setAvailable_copies(book.getAvailable_copies() + 1);
				if (book.getAvailable_copies() > 0) {
					book.setBook_status(true);
				}
				em.merge(book);
			}
			Patron patron = transaction.getPatron();
			patron.setBooks_issued(patron.getBooks_issued() - books.size());
			em.merge(patron);
			mailContents.append("\r\rThank you!");
			sendMail(transaction.getPatron().getPatron_email(), "Team - 13 - Library Management - Summary of recent return transaction", mailContents.toString());
			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			tx.rollback();
		} finally {
			em.close();
		}
	}

	private List<IssueBook> getBooksPending(int patron_id) {
		System.out.println("in getBooksPending");
		EntityManager em = EMF.get().createEntityManager();
		Query query = em.createQuery("SELECT e FROM IssueBook e WHERE e.id.patron_id = :patron_id AND e.actual_return_date IS NULL");
		query.setParameter("patron_id", patron_id);
		@SuppressWarnings("unchecked")
		List<IssueBook> list = query.getResultList();
		System.out.println("IssueBook Size: " + list.size());
		em.close();
		return list;
	}

	private void performCheckoutTransaction(Transaction transaction) {
		EntityManager em = EMF.get().createEntityManager();
		EntityTransaction tx = em.getTransaction();
		List<Book> books = transaction.getBooks();
		tx.begin();
		StringBuilder mailContents = new StringBuilder();
		mailContents.append("Hi, " + transaction.getPatron().getPatron_name() + ", \r");
		mailContents.append("Thank you for using Team - 13 Library Management System. Here's the summary of your recent transaction: \r\r");
		mailContents.append("\r\r Transaction Date: " + new java.util.Date().toLocaleString());
		try {
			int i = 1;
			for (Book book : books) {
				IssueBook issue = prepareIssueBook(book, transaction.getPatron().getPatron_id());
				book.setAvailable_copies(book.getAvailable_copies() - 1);
				if (book.getAvailable_copies() == 0) {
					book.setBook_status(false);
				}
				em.merge(issue);
				em.merge(book);
				mailContents.append("\r\r" + i + ") Book: " + book.getTitle() + "\t\tCheckout Date:" + issue.getId().getIssue_date().toLocaleString() + "\t\t Due Date: " + issue.getDue_date().toLocaleString());
				i++;
			}
			Patron patron = transaction.getPatron();
			patron.setBooks_issued(patron.getBooks_issued() + books.size());
			em.merge(patron);
			tx.commit();
			mailContents.append("\r\rThank you!");
			sendMail(transaction.getPatron().getPatron_email(), "Team - 13 - Library Management - Summary of recent checkout transaction", mailContents.toString());
		} catch (Exception e) {
			e.printStackTrace();
			tx.rollback();
		} finally {
			em.close();
		}
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
		Query query = em
				.createQuery("SELECT e FROM IssueBook e WHERE e.id.issue_date LIKE \'%"+ df.format(new java.util.Date()) + "%\'" +" AND e.id.patron_id = :patron_id");
		//query.setParameter("issue_date", df.format(new java.util.Date()) + "%");
		query.setParameter("patron_id", patron_id);
		int count = query.getResultList().size();
		em.close();
		return count;
	}
}
