package edu.cmpe275.team13.beans;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "waitlist")
public class Waitlist {
	
	@EmbeddedId
	private WaitlistId id;
	
	
	public Waitlist() {
		this.id = null;
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
}
