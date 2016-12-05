package edu.cmpe275.team13.persistence;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.springframework.stereotype.Service;

import edu.cmpe275.team13.beans.Book;
import edu.cmpe275.team13.exceptions.BookNotFoundException;
import edu.cmpe275.team13.exceptions.DeleteBookNotPermitted;



@Service
public class BookDAOImpl implements BookDAO {


//    @PersistenceContext
//    EntityManager em;
    
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
    	//FIXME 
        return null;
    }

    /**
     * returns the book with given Id from the system.
     */
    @Override
    public Book getBookById(Long isbn) {
        EntityManager em = EMF.get().createEntityManager();
        Book book = em.find(Book.class, isbn);
        if(null == book) {
        	throw new BookNotFoundException("Book with ISBN " + isbn + " not found!");
        }
        return book;
    }

    /**
     * deletes an book from the system.
     */
    @Override
    public void removeBook(Long isbn) {
        EntityManager em = EMF.get().createEntityManager();
        Book book = em.find(Book.class, isbn);
        if(null == book) {
            throw new BookNotFoundException("Book with ISBN " + isbn + " not found in the system!");
        }
        if(book.getNumber_of_copies() != book.getAvailable_copies()) {
        	throw new DeleteBookNotPermitted("Book with ISBN " + isbn  + " can not be deleted!");
        }
        em.getTransaction().begin();
        em.remove(book);
        em.getTransaction().commit();
    }
}

