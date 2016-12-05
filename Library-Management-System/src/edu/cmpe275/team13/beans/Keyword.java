package edu.cmpe275.team13.beans;

/*
 * CREATE TABLE keywords(
 * 		isbn INT(13), 
 * 		keyword VARCHAR(255), 
 * 		PRIMARY KEY(isbn, keyword), 
 * 		FOREIGN KEY(isbn) REFERENCES book(isbn) ON DELETE CASCADE);
 */
public class Keyword {

	private long isbn;
	private String keyword;
	
	public Keyword() {
		this.isbn = 0;
		this.keyword = null;
	}

	public Keyword(long isbn, String keyword) {
		super();
		this.isbn = isbn;
		this.keyword = keyword;
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
	 * @return the keyword
	 */
	public String getKeyword() {
		return keyword;
	}

	/**
	 * @param keyword the keyword to set
	 */
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
}
