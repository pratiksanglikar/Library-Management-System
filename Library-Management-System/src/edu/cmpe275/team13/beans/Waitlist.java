package edu.cmpe275.team13.beans;

import java.sql.Timestamp;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "waitlist")
public class Waitlist {
	
	@EmbeddedId
	private WaitlistId id;
	
	private Timestamp join_date;
	
	
	public Waitlist() {
		this.id = null;
		this.setJoin_date(null);

	}
	
	public Waitlist(WaitlistId id, Timestamp ts) {
		this.id = id;
		this.setJoin_date(ts);
	}

	/**
	 * @return the id
	 */
	public WaitlistId getId() {
		return id;
	}


	/**
	 * @param id the id to set
	 */
	public void setId(WaitlistId id) {
		this.id = id;
	}

	public Timestamp getJoin_date() {
		return join_date;
	}

	public void setJoin_date(Timestamp join_date) {
		this.join_date = join_date;
	}
}
