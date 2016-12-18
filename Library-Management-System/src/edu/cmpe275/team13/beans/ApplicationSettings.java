package edu.cmpe275.team13.beans;

import java.sql.Date;

import org.springframework.stereotype.Service;

@Service
public class ApplicationSettings {
	
	private Date date;

	public Date getDate() {
		return date;
	}

	public void setAppDate(Date appDate) {
		this.date = appDate;
	}
}
