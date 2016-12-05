package edu.cmpe275.team13.beans;

import java.sql.Date;
import java.util.List;

/*
 * CREATE TABLE book(
 * 		isbn INT(13), 
 * 		author_name VARCHAR(255) NOT NULL, 
 * 		title VARCHAR(255) NOT NULL, 
 * 		call_number VARCHAR(150), 
 * 		publisher_name VARCHAR(255) NOT NULL, 
 * 		year_of_publication DATE, 
 * 		location_in_library VARCHAR(255) NOT NULL, 
 * 		number_of_copies INT NOT NULL, 
 * 		book_status BOOLEAN , 
 * 		image VARCHAR(255), 
 * 		available_copies INT NOT NULL, 
 * 		created_by INT NOT NULL, 
 * 		updated_by INT, 
 * 		
 * 		PRIMARY KEY(isbn), 
 * 		FOREIGN KEY(created_by) REFERENCES librarian(librarian_id) ON DELETE NO ACTION,
 * 		FOREIGN KEY(updated_by) REFERENCES librarian(librarian_id) ON DELETE SET NULL); 
 */
public class Book {

	private long isbn;
	private String author_name;
	private String title;
	private String call_number;
	private String publisher_name;
	private Date year_of_publication;
	private String location_in_library;
	private int number_of_copies;
	private boolean book_status;
	private String image;
	private int available_copies;
	private Librarian created_by;
	private Librarian updated_by;
	private List<Keyword> keywords;

	public Book() {
		this.isbn = -1;
		this.author_name = null;
		this.title = null;
		this.call_number = null;
		this.publisher_name = null;
		this.year_of_publication = new Date(0);
		this.location_in_library = null;
		this.number_of_copies = 0;
		this.book_status = false;
		this.image = null;
		this.available_copies = 0;
		this.created_by = null;
		this.updated_by = null;
		this.keywords = null;
	}

	public Book(long isbn, String author_name, String title, String call_number, 
			String publisher_name, Date year_of_publication, String location_in_library,
			int number_of_copies, boolean book_status, String image, 
			int available_copies, Librarian created_by, Librarian updated_by,
			List<Keyword> keywords) {
		super();
		this.isbn = isbn;
		this.author_name = author_name;
		this.title = title;
		this.call_number = call_number;
		this.publisher_name = publisher_name;
		this.year_of_publication = year_of_publication;
		this.location_in_library = location_in_library;
		this.number_of_copies = number_of_copies;
		this.book_status = book_status;
		this.image = image;
		this.available_copies = available_copies;
		this.created_by = created_by;
		this.updated_by = updated_by;
		this.keywords = keywords;
	}

	/**
	 * @return the isbn
	 */
	public long getIsbn() {
		return isbn;
	}

	/**
	 * @param isbn the isbn to set
	 */
	public void setIsbn(long isbn) {
		this.isbn = isbn;
	}

	/**
	 * @return the author_name
	 */
	public String getAuthor_name() {
		return author_name;
	}

	/**
	 * @param author_name the author_name to set
	 */
	public void setAuthor_name(String author_name) {
		this.author_name = author_name;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the call_number
	 */
	public String getCall_number() {
		return call_number;
	}

	/**
	 * @param call_number the call_number to set
	 */
	public void setCall_number(String call_number) {
		this.call_number = call_number;
	}

	/**
	 * @return the publisher_name
	 */
	public String getPublisher_name() {
		return publisher_name;
	}

	/**
	 * @param publisher_name the publisher_name to set
	 */
	public void setPublisher_name(String publisher_name) {
		this.publisher_name = publisher_name;
	}

	/**
	 * @return the year_of_publication
	 */
	public Date getYear_of_publication() {
		return year_of_publication;
	}

	/**
	 * @param year_of_publication the year_of_publication to set
	 */
	public void setYear_of_publication(Date year_of_publication) {
		this.year_of_publication = year_of_publication;
	}

	/**
	 * @return the location_in_library
	 */
	public String getLocation_in_library() {
		return location_in_library;
	}

	/**
	 * @param location_in_library the location_in_library to set
	 */
	public void setLocation_in_library(String location_in_library) {
		this.location_in_library = location_in_library;
	}

	/**
	 * @return the number_of_copies
	 */
	public int getNumber_of_copies() {
		return number_of_copies;
	}

	/**
	 * @param number_of_copies the number_of_copies to set
	 */
	public void setNumber_of_copies(int number_of_copies) {
		this.number_of_copies = number_of_copies;
	}

	/**
	 * @return the book_status
	 */
	public boolean isBook_status() {
		return book_status;
	}

	/**
	 * @param book_status the book_status to set
	 */
	public void setBook_status(boolean book_status) {
		this.book_status = book_status;
	}

	/**
	 * @return the image
	 */
	public String getImage() {
		return image;
	}

	/**
	 * @param image the image to set
	 */
	public void setImage(String image) {
		this.image = image;
	}

	/**
	 * @return the available_copies
	 */
	public int getAvailable_copies() {
		return available_copies;
	}

	/**
	 * @param available_copies the available_copies to set
	 */
	public void setAvailable_copies(int available_copies) {
		this.available_copies = available_copies;
	}

	/**
	 * @return the created_by
	 */
	public Librarian getCreated_by() {
		return created_by;
	}

	/**
	 * @param created_by the created_by to set
	 */
	public void setCreated_by(Librarian created_by) {
		this.created_by = created_by;
	}

	/**
	 * @return the updated_by
	 */
	public Librarian getUpdated_by() {
		return updated_by;
	}

	/**
	 * @param updated_by the updated_by to set
	 */
	public void setUpdated_by(Librarian updated_by) {
		this.updated_by = updated_by;
	}

	/**
	 * @return keywords in the book.
	 */
	public List<Keyword> getKeywords() {
		return keywords;
	}

	/**
	 * @param keywords
	 */
	public void setKeywords(List<Keyword> keywords) {
		this.keywords = keywords;
	}
}
