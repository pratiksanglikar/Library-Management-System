package edu.cmpe275.team13.beans;

import java.sql.Timestamp;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "issue_book")
public class IssueBook {

	@EmbeddedId
	private IssueBookID id;
	
	private Timestamp due_date;
	
	private Timestamp actual_return_date;
	
	public IssueBook() {
		id = null;
		due_date = null;
		actual_return_date = null;
	}
	
	public IssueBook(IssueBookID id, Timestamp due_date, Timestamp actual_return_date) {
		super();
		this.id = id;
		this.due_date = due_date;
		this.actual_return_date = actual_return_date;
	}

	/**
	 * @return the id
	 */
	public IssueBookID getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(IssueBookID id) {
		this.id = id;
	}

	/**
	 * @return the due_date
	 */
	public Timestamp getDue_date() {
		return due_date;
	}

	/**
	 * @param due_date the due_date to set
	 */
	public void setDue_date(Timestamp due_date) {
		this.due_date = due_date;
	}

	/**
	 * @return the actual_return_date
	 */
	public Timestamp getActual_return_date() {
		return actual_return_date;
	}

	/**
	 * @param actual_return_date the actual_return_date to set
	 */
	public void setActual_return_date(Timestamp actual_return_date) {
		this.actual_return_date = actual_return_date;
	}
}
