package edu.cmpe275.team13.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import org.springframework.stereotype.Service;

import edu.cmpe275.team13.beans.Book;
import edu.cmpe275.team13.exceptions.BookNotFoundException;
import edu.cmpe275.team13.exceptions.DeleteBookNotPermitted;
import edu.cmpe275.team13.search.BookSearch;

@Service
public class BookDAOImpl implements BookDAO {

	// @PersistenceContext
	// EntityManager em;

	/**
	 * Adds a new book in the system.
	 */
	@Override
	public Long addBook(Book book) {
		EntityManager em = EMF.get().createEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			em.persist(book);
			em.flush();
			em.getTransaction().commit();
		} catch (Exception e) {
			Logger log = Logger.getLogger("Adding a book to DB");
			log.log(Level.WARNING, "Rolling Back:", e);
			tx.rollback();
		} finally {
			em.close();
		}
		return book.getIsbn();
	}

	/**
	 * updates the book with given ID.
	 */
	@Override
	public void updateBook(Book book) {
		EntityManager em = EMF.get().createEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			em.merge(book);
			em.getTransaction().commit();
		} catch (Exception e) {
			Logger log = Logger.getLogger("Updating a book to DB");
			log.log(Level.WARNING, "Rolling Back:", e);
			tx.rollback();
		} finally {
			em.close();
		}
	}

	/**
	 * returns all books in the system.
	 */
	@Override
	public List<Book> listBooks() {
		return null;
	}

	/**
	 * returns the book with given Id from the system.
	 */
	@Override
	public Book getBookById(Long isbn) {
		System.out.println("Getting book by id: " + isbn);
		EntityManager em = EMF.get().createEntityManager();
		Book book = em.find(Book.class, isbn);
		if (null == book) {
			throw new BookNotFoundException("Book with ISBN " + isbn + " not found!");
		}
		em.close();
		return book;
	}

	/**
	 * deletes an book from the system.
	 */
	@Override
	public void removeBook(Long isbn) {
		EntityManager em = EMF.get().createEntityManager();
		Book book = em.find(Book.class, isbn);
		if (null == book) {
			throw new BookNotFoundException("Book with ISBN " + isbn + " not found in the system!");
		}
		if (book.getNumber_of_copies() != book.getAvailable_copies()) {
			throw new DeleteBookNotPermitted("Book with ISBN " + isbn + " can not be deleted!");
		}
		em.getTransaction().begin();
		em.remove(book);
		em.getTransaction().commit();
		em.close();
	}

	@Override
	public List<Book> searchBySpec(BookSearch bookSpec) {
		if (null == bookSpec) {
			return new ArrayList<Book>(0);
		}
		EntityManager em = EMF.get().createEntityManager();
		boolean isbn_set = false, author_name_set = false, title_set = false, publisher_name_set = false,
				year_set = false, updated_by_set = false, created_by_set = false, is = false, keywords_set = false;
		String queryString = "SELECT e FROM Book e where ";
		if (bookSpec.getIsbn() != null) {
			if (!is) {
				queryString += " e.isbn = :isbn ";
				is = !is;
			} else {
				queryString += " AND e.isbn = :isbn ";
			}
			isbn_set = true;
		}
		if (bookSpec.getAuthor_name() != null && bookSpec.getAuthor_name().length() != 0) {
			if (!is) {
				queryString += " e.author_name like :author_name";
				is = !is;
			} else {
				queryString += " AND e.author_name like :author_name";
			}
			author_name_set = true;
		}
		if (bookSpec.getTitle() != null && bookSpec.getTitle().length() != 0) {
			if (!is) {
				queryString += " e.title like :title";
				is = !is;
			} else {
				queryString += " AND e.title like :title";
			}
			title_set = true;
		}
		if (bookSpec.getPublisher_name() != null && bookSpec.getPublisher_name().length() != 0) {
			if (!is) {
				queryString += " e.publisher_name like :publisher_name";
				is = !is;
			} else {
				queryString += " AND e.publisher_name like :publisher_name";
			}
			publisher_name_set = true;
		}
		if (bookSpec.getYear_of_publication() != null) {
			if (!is) {
				queryString += " e.year_of_publication = :year";
				is = !is;
			} else {
				queryString += " AND e.year_of_publication = :year";
			}
			year_set = true;
		}
		if (!is) {
			queryString += " e.book_status = :book_status";
			is = !is;
		} else {
			queryString += " AND e.book_status = :book_status";
		}
		if (bookSpec.getUpdated_by() != Integer.MIN_VALUE) {
			if (!is) {
				queryString += " e.updated_by = :updated_by";
				is = !is;
			} else {
				queryString += " AND e.updated_by = :updated_by";
			}
			updated_by_set = true;
		}
		if (bookSpec.getCreated_by() != Integer.MIN_VALUE) {
			if (!is) {
				queryString += " e.created_by = :created_by";
				is = !is;
			} else {
				queryString += " AND e.created_by = :created_by";
			}
			created_by_set = true;
		}
		if (bookSpec.getKeywords() != null && bookSpec.getKeywords().length > 0) {
			String kywrdstrng = " e.keywords like :keyword0 ";
			for (int i = 1; i < bookSpec.getKeywords().length; i++) {
				kywrdstrng += " OR e.keywords like :keyword" + i + " ";
			}
			//kywrdstrng += " )";
			if (!is) {
				queryString += kywrdstrng;
				is = !is;
			} else {
				queryString += " AND " + kywrdstrng;
			}
			keywords_set = true;
		}
		Query query = em.createQuery(queryString);
		if (isbn_set) {
			query.setParameter("isbn", bookSpec.getIsbn());
		}
		if (author_name_set) {
			query.setParameter("author_name", "%" + bookSpec.getAuthor_name() + "%");
		}
		if (title_set) {
			query.setParameter("title", "%" + bookSpec.getTitle() + "%");
		}
		if (publisher_name_set) {
			query.setParameter("publisher_name", "%" + bookSpec.getPublisher_name() + "%");
		}
		if (year_set) {
			query.setParameter("year", bookSpec.getYear_of_publication());
		}
		query.setParameter("book_status", bookSpec.getBook_status());
		if (created_by_set) {
			query.setParameter("created_by", bookSpec.getCreated_by());
		}
		if (updated_by_set) {
			query.setParameter("updated_by", bookSpec.getUpdated_by());
		}
		if (keywords_set) {
			String[] keywords = bookSpec.getKeywords();
			for (int i = 0; i < keywords.length; i++) {
				query.setParameter("keyword" + i, "%" + keywords[i] +"%");
			}
		}
		@SuppressWarnings("unchecked")
		List<Book> list = query.getResultList();
		em.close();
		return list;
	}
}
