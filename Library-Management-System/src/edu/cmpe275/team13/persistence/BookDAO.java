package edu.cmpe275.team13.persistence;

import java.util.List;

import edu.cmpe275.team13.beans.Book;

public interface BookDAO {

	public Long addBook(Book book);

	public void updateBook(Book book);

	public List<Book> listBooks();

	public Book getBookById(Long isbn);

	public void removeBook(Long isbn);
	
}
