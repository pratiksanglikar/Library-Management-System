package edu.cmpe275.team13.beans;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Embeddable;

@Embeddable
public class WaitlistId implements Serializable {
	
	private static final long serialVersionUID = 300376766958883554L;
	
	private Long isbn;
	private int patron_id;
	private Timestamp join_date;
	
	public WaitlistId() {
		isbn = null;
		patron_id = Integer.MIN_VALUE;
		join_date = null;
	}

	public WaitlistId(Long isbn, int patron_id, Timestamp join_date) {
		super();
		this.isbn = isbn;
		this.patron_id = patron_id;
		this.join_date = join_date;
	}

	/**
	 * @return the isbn
	 */
	public Long getIsbn() {
		return isbn;
	}

	/**
	 * @param isbn the isbn to set
	 */
	public void setIsbn(Long isbn) {
		this.isbn = isbn;
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
	 * @return the join_date
	 */
	public Timestamp getJoin_date() {
		return join_date;
	}

	/**
	 * @param join_date the join_date to set
	 */
	public void setJoin_date(Timestamp join_date) {
		this.join_date = join_date;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((isbn == null) ? 0 : isbn.hashCode());
		result = prime * result + ((join_date == null) ? 0 : join_date.hashCode());
		result = prime * result + patron_id;
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WaitlistId other = (WaitlistId) obj;
		if (isbn == null) {
			if (other.isbn != null)
				return false;
		} else if (!isbn.equals(other.isbn))
			return false;
		if (join_date == null) {
			if (other.join_date != null)
				return false;
		} else if (!join_date.equals(other.join_date))
			return false;
		if (patron_id != other.patron_id)
			return false;
		return true;
	}
}
