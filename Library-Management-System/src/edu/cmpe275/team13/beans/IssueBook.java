package edu.cmpe275.team13.beans;

import java.sql.Date;

/*
 * CREATE TABLE issue_book(
 * 		patron_id INT(6), 
 * 		isbn INT(13), 
 * 		issue_date DATETIME, 
 * 		due_date DATETIME NOT NULL, 
 * 		actual_return_date DATETIME, 
 * 		PRIMARY KEY(patron_id, isbn, issue_date), 
 * 		FOREIGN KEY(patron_id) REFERENCES patron(patron_id) ON DELETE NO ACTION, 
 * 		FOREIGN KEY(isbn) REFERENCES book(isbn) ON DELETE NO ACTION);
 */
public class IssueBook {

	private int patron_id;
	private long isbn;
	private Date issue_date;
	private Date due_date;
	private Date actual_return_date;
	
	/**
	 * 
	 */
	public IssueBook() {
		this.patron_id = 0;
		this.isbn = 0;
		this.issue_date = null;
		this.due_date = null;
		this.actual_return_date = null;
	}

	/**
	 * 
	 * @param patron_id
	 * @param isbn
	 * @param issue_date
	 * @param due_date
	 * @param actual_return_date
	 */
	public IssueBook(int patron_id, long isbn, Date issue_date, Date due_date, Date actual_return_date) {
		super();
		this.patron_id = patron_id;
		this.isbn = isbn;
		this.issue_date = issue_date;
		this.due_date = due_date;
		this.actual_return_date = actual_return_date;
	}

	/**
	 * @return the patron_id
	 */
	public int getPatron_id() {
		return patron_id;
	}

	/**
	 * @param patron_id the patron_id to set
	 */
	public void setPatron_id(int patron_id) {
		this.patron_id = patron_id;
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
	 * @return the issue_date
	 */
	public Date getIssue_date() {
		return issue_date;
	}

	/**
	 * @param issue_date the issue_date to set
	 */
	public void setIssue_date(Date issue_date) {
		this.issue_date = issue_date;
	}

	/**
	 * @return the due_date
	 */
	public Date getDue_date() {
		return due_date;
	}

	/**
	 * @param due_date the due_date to set
	 */
	public void setDue_date(Date due_date) {
		this.due_date = due_date;
	}

	/**
	 * @return the actual_return_date
	 */
	public Date getActual_return_date() {
		return actual_return_date;
	}

	/**
	 * @param actual_return_date the actual_return_date to set
	 */
	public void setActual_return_date(Date actual_return_date) {
		this.actual_return_date = actual_return_date;
	}
}
